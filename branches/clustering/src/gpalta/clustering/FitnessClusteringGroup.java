/*
 * NewClass.java
 *
 * Created on 8 de junio de 2006, 06:33 PM
 *
 * Copyright (C) 2006 Neven Boric <nboric@gmail.com>
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

/**
 * @author neven
 */
public class FitnessClusteringGroup extends FitnessGroup
{
    protected double[][] prototypes;
    protected int[] winner;
    protected int[] nPerCluster;
    protected Config config;

    public void init(Config config, DataHolder data, String fileName)
    {
        init(config, data, null, null);
    }

    public void init(Config config, DataHolder data, Output desiredOutputs, double[] weights)
    {
        this.config = config;
        winner = new int[data.nSamples];
    }

    public void calculate(Output outputs, Individual ind, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        calcProto(outputs, data);

        //calculate total error:
        double error = 0;
        double[] x;
        int nClusters = outputs.getDim();
        TreeGroup ind2 = (TreeGroup)ind;
        if (ind2.nTrees() != nClusters)
            System.out.println("a");
        double[] protoError = new double[nClusters];
        for (int wSample = 0; wSample < data.nSamples; wSample++)
        {
            x = data.getSample(wSample);
            //protoError[winner[wSample]] += Common.dist2(prototypes[winner[wSample]], x);
            double sampleError = 0;
            for (int wVar=0; wVar<data.nVars; wVar++)
                sampleError += Math.abs(prototypes[winner[wSample]][wVar]-x[wVar])/data.getRange(wVar+1);
            protoError[winner[wSample]] += sampleError/data.nVars;
        }
        for (int wCluster = 0; wCluster < nClusters; wCluster++)
        {
            error += protoError[wCluster];
            if (nPerCluster[wCluster] != 0)
            {
                protoError[wCluster] = 1 / (1 + protoError[wCluster]/nPerCluster[wCluster]);
            }
            else
            {
                protoError[wCluster] = 0;
            }
            ind2.setSamplesWon(wCluster, nPerCluster[wCluster]);
        }

        error /= data.nSamples;

        double error2 = 0;

        for (int i = 0; i<nClusters-1; i++)
        {
            for (int j=i+1; j<nClusters; j++)
            {
                double sampleError = 0;
                for (int wVar=0; wVar<data.nVars; wVar++)
                    sampleError += Math.abs(prototypes[i][wVar]-prototypes[j][wVar])/data.getRange(wVar+1);
                error2 += nPerCluster[i]*nPerCluster[j]*sampleError/data.nVars;
            }
        }

        int sumW = 0;
        for (int i = 0; i<nClusters-1; i++)
            if (nPerCluster[i] != 0)
                for (int j=i+1; j<nClusters; j++)
                    if (nPerCluster[j] != 0)
                        sumW += nPerCluster[i]*nPerCluster[j];

        if (sumW != 0)
            error2 /= sumW;
        else
            error2 = Double.MIN_VALUE;

        assignFitness(ind,1/(1+error/error2), protoError, config);
    }

    protected void calcProto(Output outputs, DataHolder data)
    {
        int nClusters = outputs.getDim();
        double prob[][] = new double[nClusters][0];
        for (int wCluster = 0; wCluster < nClusters; wCluster++)
            prob[wCluster] = outputs.getArray(wCluster);

        nPerCluster = new int[nClusters];
        for (int wSample=0; wSample< data.nSamples; wSample++)
        {
            double max = prob[0][wSample];
            winner[wSample] = 0;
            for (int wCluster = 1; wCluster <nClusters; wCluster++)
            {
                if (prob[wCluster][wSample] > max)
                {
                    max = prob[wCluster][wSample];
                    winner[wSample] = wCluster;
                }
            }
            nPerCluster[winner[wSample]]++;
        }

        prototypes = new double[nClusters][data.nVars];
        double[] x;
        for (int wSample=0; wSample < data.nSamples; wSample++)
        {
            x = data.getSample(wSample);
            for (int wVar = 0; wVar < data.nVars; wVar++)
            {
                prototypes[winner[wSample]][wVar] += x[wVar];
            }
        }
        for (int wCluster = 0; wCluster <nClusters; wCluster++)
            if (nPerCluster[wCluster] != 0)
                for (int wVar = 0; wVar < data.nVars; wVar++)
                    prototypes[wCluster][wVar] /= nPerCluster[wCluster];
    }

    public Output getProcessedOutput(Output raw, Individual ind, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        int nClusters = raw.getDim();
        ClusteringOutput processed = new ClusteringOutput(nClusters, data.nSamples);
        double[][] prob = new double[nClusters][];
        for (int i = 0; i < nClusters; i++)
        {
            processed.setArray(i, raw.getArrayCopy(i));
            prob[i] = raw.getArrayCopy(i);
        }
        calcProto(raw, data);
        processed.setPrototypesCopy(prototypes);
        processed.setPertenenceCopy(prob);
        return processed;
    }

}
