/*
 * FitnessClusteringCS.java
 *
 * Copyright (C) 2006, 2007 Neven Boric <nboric@gmail.com>
 *
 * This file is part of GPalta.
 *
 * GPalta is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * GPalta is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPalta; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package gpalta.clustering;

import gpalta.core.*;
import gpalta.multitree.MultiOutput;
import gpalta.multitree.MultiTreeIndividual;

import java.io.IOException;
import java.io.Serializable;

public class FitnessClusteringCS implements Fitness, Serializable, Cloneable
{
    private double[][] kernel;
    private double[][] prob;
    private double[][] probT;
    private Config config;
    private int[] target;
    private int[] winner;
    private boolean first;
    private int callingThread;
    private int[] allSamples;
    private int[][] close;

    private boolean useNative;
    private static boolean isNativeLibLoaded;
    private static final String NATIVE_LIB_DEF_NAME = "NativeFitnessClusteringCS";  

    private static void loadNativeFitness(String nativeFitnessLib)
    {
        for (int i=0;i <2; i++)
        {
            try
            {
                if(i == 0 && nativeFitnessLib != null)
                {
                    System.load(nativeFitnessLib);
                    Logger.log("Loaded native fitness library from " + nativeFitnessLib);
                }
                else if (i==1)
                {
                    System.loadLibrary(NATIVE_LIB_DEF_NAME);
                    Logger.log("Loaded native fitness library from default location: " + NATIVE_LIB_DEF_NAME);
                }
                isNativeLibLoaded = true;
            }
            catch (UnsatisfiedLinkError e)
            {

            }
        }
    }

    public Object clone() throws CloneNotSupportedException
    {
        FitnessClusteringCS f = (FitnessClusteringCS)super.clone();
        f.kernel = Common.copy(kernel);
        f.close = Common.copy(close);
        f.prob = null;
        if (config.useHits)
        {
            f.target = Common.copy(target);
            f.winner = Common.copy(winner);
        }
        return f;
    }

    public void setCallingThread(int wThread)
    {
        callingThread = wThread;
    }

    public void init(Config config, ProblemData problemData, String fileName)
    {
        SingleOutput desOutput = new SingleOutput(problemData.nSamples);
        double[][] desOutputMatrix = null;
        try
        {
            /* Use any separator, there should be exactly one value per line */
            desOutputMatrix = Common.readFromFile(fileName, ",");
        }
        catch (IOException e)
        {
            Logger.log(e);
        }

        if (desOutputMatrix.length != problemData.nSamples || desOutputMatrix[0].length != 1)
        {
            Logger.log("Warning: wrong size when reading desired outputs from " + fileName);
        }

        /* Common.readFromFile() returns a matrix
         * We need to turn it into a vector
         */
        desOutput.store(Common.transpose(desOutputMatrix)[0]);
        init(config, problemData, desOutput, null);
    }

    public void init(Config config, ProblemData problemData, Output desiredOutputs, double[] weights)
    {
        this.config = config;

        double sigmaOpt2;
        if (config.sigma == 0)
        {
            double[][] data = new double[problemData.nVars][];
            for (int i=0; i<problemData.nVars; i++)
            {
                data[i] = problemData.getDataVect(i+1);
            }
            sigmaOpt2 = InformationTheory.sigmaOpt2(data);
        }
        else
        {
            sigmaOpt2 = config.sigma*config.sigma;
        }
        problemData.sigmaOpt2 = sigmaOpt2;

        double limit;
        if (config.useNeighborhood)
        {
            //limit = InformationTheory.paretoRadius(pdist, false);
            limit = 3.5*Math.sqrt(sigmaOpt2);
        }
        else
            limit = Double.MAX_VALUE;

        kernel = new double[problemData.nSamples][];
        close = new int[problemData.nSamples][];
        double[] dx = new double[problemData.nVars];
        double[] x1;
        double[] x2;
        for (int i = 0; i < problemData.nSamples; i++)
        {
            int n = 0;
            x1 = problemData.getSample(i);
            for (int j = 0; j < problemData.nSamples; j++)
            {
                x2 = problemData.getSample(j);
                if (Common.dist2(x1, x2) <= limit)
                {
                    n++;
                }
            }
            kernel[i] = new double[n];
            close[i] = new int[n];
            int nAdded = 0;
            for (int j = 0; j < problemData.nSamples; j++)
            {
                x2 = problemData.getSample(j);
                if (Common.dist2(x1, x2) <= limit)
                {
                    for (int wVar=0; wVar< problemData.nVars; wVar++)
                    {
                        dx[wVar] = x1[wVar] - x2[wVar];
                    }
                    kernel[i][nAdded] = InformationTheory.gaussianKernel(dx, 2*sigmaOpt2);
                    close[i][nAdded] = j;
                    nAdded++;
                }
            }
        }
        if (config.useHits)
        {
            target = new int[problemData.nSamples];
            for (int i=0; i< problemData.nSamples; i++)
            {
                target[i] = (int)((SingleOutput)desiredOutputs).x[i];
            }
        }
        if (config.useHits || config.discretize)
        {
            winner = new int[problemData.nSamples];
        }

        if (config.useNativeFitness)
        {
            if (!isNativeLibLoaded)
                loadNativeFitness(config.nativeFitnessLib);
            if (isNativeLibLoaded)
            {
                useNative = true;
                Logger.log("Using native fitness lib");
            }
        }
        first = true;
        allSamples = new int[problemData.nSamples];
        for (int i = 0; i < allSamples.length; i++)
        {
            allSamples[i] = i;
        }
    }

    public native double[] calculateNative(int wThread, double[][] outputs, int nSamples, int nClusters, int[] samples, int nSubSamples, int useHits, int normalizeOutputs, int discretize);
    public native void initNative(int wThread, double[][] kernel, int nSamples, int nClusters, int[][] close, int[] nClose);
    
    public double[] calculateNativeInterface(Output outputs, ProblemData problemData, int[] wSamples)
    {
        int nClusters = outputs.getDim();
        if (first)
        {
            first = false;
            int[] nClose = new int[problemData.nSamples];
            for (int i = 0; i < nClose.length; i++)
            {
                nClose[i] = kernel[i].length;
            }
            initNative(callingThread, kernel, problemData.nSamples, nClusters, close, nClose);
        }

        double[][] out = new double[nClusters][];
        for (int wCluster=0; wCluster<nClusters; wCluster++)
        {
            out[wCluster] = ((MultiOutput)outputs).getArray(wCluster);
        }

        if (wSamples == null)
        {
            wSamples = allSamples;
        }
        int nSubSamples = wSamples.length;
        int useHits = config.useHits? 1:0;
        int normalizeOutputs = config.normalizeOutputs ? 1:0;
        int discretize = config.discretize ? 1:0;

        return calculateNative(callingThread, out, problemData.nSamples, nClusters, wSamples, nSubSamples, useHits, normalizeOutputs, discretize);
    }

    public double[] calculate(Output outputs, Individual ind, ProblemData problemData, int[] wSamples)
    {
        if (useNative)
        {
            return calculateNativeInterface(outputs, problemData, wSamples);
        }

        calcProb(outputs, problemData);

        int nClusters = outputs.getDim();
        double[] cInfo = new double[nClusters];

        if (wSamples == null)
        {
            wSamples = allSamples;
        }
        int nSubSamples = wSamples.length;
        for (int wCluster=0; wCluster<nClusters; wCluster++)
        {
            double[] probCluster = prob[wCluster];
            for (int i=0; i<nSubSamples; i++)
            {
                int ii = wSamples[i];
                int[] closeii = close[ii];
                double[] kernelii = kernel[ii];
                double cInfoi = 0;
                for (int j = 0; j < closeii.length; j++)
                {
                    cInfoi += probCluster[closeii[j]]*kernelii[j];
                }
                cInfo[wCluster] += probCluster[ii]*cInfoi;
            }
        }

        double info = 0;
        double sumKernel = 0;

        if (probT != null && probT.length == nSubSamples && probT[0].length == nClusters)
            Common.transpose(probT, prob);
        else
            probT = Common.transpose(prob);

        for (int i=0; i<nSubSamples; i++)
        {
            double[] infoi = new double[nClusters];
            int ii = wSamples[i];
            int[] closeii = close[ii];
            double[] kernelii = kernel[ii];
            for (int j = 0; j < closeii.length; j++)
            {
                double[] probTj = probT[closeii[j]];
                double kerneliij = kernelii[j];
                for (int wCluster=0; wCluster<nClusters; wCluster++)
                {
                    infoi[wCluster] += probTj[wCluster]*kerneliij;
                }
                sumKernel += kerneliij;
            }
            info += Common.dotProduct(probT[ii], infoi);
        }
        info = sumKernel - info;

        for (int wCluster = 0; wCluster < nClusters; wCluster++)
        {
            info /= Math.sqrt(cInfo[wCluster]);
        }
        info /= 2;

        double[] fit = new double[nClusters + 2];
        fit[0] = -info;

        if (config.useHits)
        {
            fit[1] = (double)hits(winner, target, nClusters)/problemData.nSamples;
        }

        /*
        if (config.useHits)
        {
            int[] nPerCluster = new int[nClusters];
            for (int i=0; i<nSubSamples; i++)
            {
                int ii = wSamples[i];
                nPerCluster[winner[ii]]++;
            }
            for (int wCluster=0; wCluster<nClusters; wCluster++)
            {
                cInfo[wCluster] = 0;
                for (int i=0; i<nSubSamples; i++)
                {
                    int ii = wSamples[i];
                    closeii = close[ii];
                    kernelii = kernel[ii];
                    if (winner[ii] == wCluster)
                    {
                        for (int j = 0; j < closeii.length; j++)
                        {
                            int jj = closeii[j];
                            if (winner[jj] == wCluster)
                                cInfo[wCluster] += kernelii[jj];
                        }
                    }
                }
                if (nPerCluster[wCluster] != 0)
                    cInfo[wCluster] /= Math.pow(nPerCluster[wCluster], 2);
            }
        }
        else
        {
            for (int wCluster = 0; wCluster < nClusters; wCluster++)
            {
                double sumProb = 0;
                probCluster = prob[wCluster];
                for (int i=0; i<nSubSamples; i++)
                {
                    int ii = wSamples[i];
                    closeii = close[ii];
                    for (int j = 0; j < closeii.length; j++)
                    {
                        int jj = closeii[j];
                        sumProb += probCluster[ii]*probCluster[jj];
                    }
                }
                cInfo[wCluster] /= sumProb;
                //cInfo[wCluster] /= Math.pow(Common.sum(prob[wCluster]), 2);
            }
        }
        */

        System.arraycopy(cInfo, 0, fit, 2, nClusters);
        return fit;
    }

    public void assign(Individual ind, double[] fit)
    {
        ind.setFitness(fit[0]);
        ind.hits = fit[1];
        for (int wCluster=0; wCluster<((MultiTreeIndividual)ind).nTrees(); wCluster++)
        {
            ((MultiTreeIndividual)ind).getTree(wCluster).setFitness(fit[2+wCluster]);
        }
    }

    private void calcProb(Output outputs, ProblemData problemData)
    {
        int nClusters = outputs.getDim();
        int nSamples = problemData.nSamples;

        if (prob==null || prob.length != nClusters || prob[0]==null)
            prob = new double[nClusters][nSamples];

        double[] probCluster;
        for (int wCluster = 0; wCluster < nClusters; wCluster++)
        {
            System.arraycopy(((MultiOutput)outputs).getArray(wCluster),0,prob[wCluster],0,nSamples);
        }

        if (config.useHits || config.discretize)
        {
            for (int wSample=0; wSample< nSamples; wSample++)
            {
                winner[wSample] = 0;
                double max = prob[0][wSample];
                for (int wCluster = 1; wCluster <nClusters; wCluster++)
                {
                    if (prob[wCluster][wSample] > max)
                    {
                        max = prob[wCluster][wSample];
                        winner[wSample] = wCluster;
                    }
                }
            }
        }

        if (config.normalizeOutputs)
        {
            for (int wCluster = 0; wCluster < nClusters; wCluster++)
            {
                Common.sigmoid(prob[wCluster]);
            }
        }

        if (config.discretize)
        {
            for (int wSample=0; wSample< nSamples; wSample++)
            {
                for (int wCluster = 0; wCluster <nClusters; wCluster++)
                {
                    prob[wCluster][wSample] = 0;
                }
                prob[winner[wSample]][wSample] = 1;
            }
        }

        for (int wCluster = 0; wCluster <nClusters; wCluster++)
        {
            probCluster = prob[wCluster];
            for (int wSample=0; wSample< nSamples; wSample++)
            {
                probCluster[wSample] += 0.05;
                if (probCluster[wSample] > 1)
                    probCluster[wSample] = 1;
            }
        }


        for (int wSample=0; wSample< nSamples; wSample++)
        {
            double sum = prob[0][wSample];
            for (int wCluster = 1; wCluster <nClusters; wCluster++)
            {
                sum += prob[wCluster][wSample];
            }
            for (int wCluster = 0; wCluster <nClusters; wCluster++)
            {
                prob[wCluster][wSample] /= sum;
            }
        }
    }

    public Output getProcessedOutput(Output raw, ProblemData problemData)
    {
        int nClusters = raw.getDim();
        ClusteringOutput processed = new ClusteringOutput(nClusters, problemData.nSamples);
        for (int i = 0; i < nClusters; i++)
        {
            processed.store(i, ((MultiOutput)raw).getArrayCopy(i));
        }
        calcProb(raw, problemData);
        processed.setPertenenceCopy(prob);
        return processed;
    }

    public int hits(int[] system, int[] actual, int nClasses)
    {
        int hits = 0;
        int[][] assigned = new int[nClasses][nClasses];
        for (int i=0; i<system.length; i++)
        {
            if (actual[i] >= nClasses)
                continue;
            assigned[actual[i]][system[i]]++;
        }
        boolean done = false;
        int[] eqA2S = new int[nClasses];
        int[] eqS2A = new int[nClasses];
        for (int i=0; i<nClasses; i++)
        {
            eqA2S[i] = -1;
            eqS2A[i] = -1;
        }
        while(!done)
        {

            for (int ac=0; ac<nClasses; ac++)
            {
                if (eqA2S[ac] != -1)
                    continue;
                int max = -1;
                int winner = 0;
                for (int sc=0; sc<nClasses; sc++)
                {
                    if (eqS2A[sc] != -1)
                        continue;
                    if(assigned[ac][sc] > max)
                    {
                        max = assigned[ac][sc];
                        winner = sc;
                    }
                }
                int winner2 = ac;
                for (int ac2=0; ac2<nClasses; ac2++)
                {
                    if (eqA2S[ac2] != -1)
                        continue;
                    if(assigned[ac2][winner] > max)
                    {
                        max = assigned[ac2][winner];
                        winner2 = ac2;
                    }
                }
                if (winner2 == ac)
                {
                    eqA2S[ac] = winner;
                    eqS2A[winner] = ac;
                }
            }
            done = true;
            for (int i=0; i<nClasses; i++)
            {
                if (eqA2S[i]==-1)
                {
                    done = false;
                    break;
                }
            }
        }
        for (int i=0; i<system.length; i++)
        {
            if (actual[i] >= nClasses)
                continue;
            if (system[i] == eqA2S[actual[i]])
            {
                hits++;
            }
        }
        return hits;
    }

}
