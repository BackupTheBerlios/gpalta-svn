package gpalta.clustering;

import gpalta.core.*;
import gpalta.multitree.MultiOutput;
import gpalta.multitree.MultiTreeIndividual;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author neven
 */
public class FitnessClusteringCS implements Fitness, Serializable, Cloneable
{
    private double[][] kernel;
    private double[][] prob;
    private Config config;
    private int[] target;
    private int[] winner;
    private int[][] close;

    public Object clone() throws CloneNotSupportedException
    {
        FitnessClusteringCS f = (FitnessClusteringCS)super.clone();
        f.kernel = Common.copy(kernel);
        f.prob = null;
        if (config.useHits)
        {
            f.target = Common.copy(target);
            f.winner = Common.copy(winner);
        }
        f.close = Common.copy(close);
        return f;
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

        kernel = new double[problemData.nSamples][problemData.nSamples];
        double[] dx = new double[problemData.nVars];
        double[] x1;
        double[] x2;
        for (int i=0; i< problemData.nSamples; i++)
        {
            x1 = problemData.getSample(i);
            for (int j=0; j< problemData.nSamples; j++)
            {
                x2 = problemData.getSample(j);
                for (int wVar=0; wVar< problemData.nVars; wVar++)
                {
                    dx[wVar] = x1[wVar] - x2[wVar];
                }
                kernel[i][j] = InformationTheory.gaussianKernel(dx, 2*sigmaOpt2);
            }
        }
        if (config.useHits)
        {
            target = new int[problemData.nSamples];
            for (int i=0; i< problemData.nSamples; i++)
            {
                target[i] = (int)((SingleOutput)desiredOutputs).x[i];
            }
            winner = new int[problemData.nSamples];
        }

        double[][] pdist = new double[problemData.nSamples][problemData.nSamples];
        for (int s1=0; s1 < problemData.nSamples-1; s1++)
        {
            for (int s2=s1+1; s2<problemData.nSamples; s2++)
            {
                pdist[s1][s2] = pdist[s2][s1] = Common.dist2(problemData.getSample(s1), problemData.getSample(s2));
            }
        }

        double limit = InformationTheory.paretoRadius(pdist, false);
        //double limit = 1E-3;
        close = new int[problemData.nSamples][];

        for (int i = 0; i < problemData.nSamples; i++)
        {
            int n = 0;
            for (int j = 0; j < problemData.nSamples; j++)
            {
                if (pdist[i][j] <= limit)
                    n++;
            }
            close[i] = new int[n];
            int nAdded = 0;
            for (int j = 0; j < problemData.nSamples; j++)
            {
                if (pdist[i][j] <= limit)
                    close[i][nAdded++] = j;
            }
        }
    }

    public double[] calculate(Output outputs, Individual ind, ProblemData problemData)
    {
        calcProb(outputs, problemData);

        int nClusters = outputs.getDim();
        double[] cInfo = new double[nClusters];

        int[] samples = Common.randPerm(problemData.nSamples);

        int nSubSamples = (int)(config.subSamplingRatio * problemData.nSamples);

        for (int wCluster=0; wCluster<nClusters; wCluster++)
        {
            for (int i=0; i<nSubSamples; i++)
            {
                int ii = samples[i];
                for (int j = 0; j < close[ii].length; j++)
                {
                    int jj = close[ii][j];
                    cInfo[wCluster] += prob[wCluster][ii]*prob[wCluster][jj]*kernel[ii][jj];
                }
            }
        }

        double info = 0;

        double[][] probT = Common.transpose(prob);

        for (int i=0; i<nSubSamples; i++)
        {
            int ii = samples[i];
            for (int j = 0; j < close[ii].length; j++)
            {
                int jj = close[ii][j];
                info += (1-Common.dotProduct(probT[ii], probT[jj]))*kernel[ii][jj];
            }
        }

        for (int wCluster = 0; wCluster < nClusters; wCluster++)
        {
            info /= Math.sqrt(cInfo[wCluster]);
        }
        info /= 2;

        double[] fit = new double[nClusters + 2];
        fit[0] = 1/(1+info);
        if (config.useHits)
        {
            fit[1] = (double)hits(winner, target, nClusters)/problemData.nSamples;
        }
        for (int wCluster = 0; wCluster < nClusters; wCluster++)
        {
            cInfo[wCluster] /= Math.pow(Common.sum(prob[wCluster]), 2);
            fit[2+wCluster] = cInfo[wCluster];
            ((MultiTreeIndividual)ind).getTree(wCluster).setFitness(cInfo[wCluster]);
        }
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

    protected void calcProb(Output outputs, ProblemData problemData)
    {
        int nClusters = outputs.getDim();
        prob = new double[nClusters][];
        for (int wCluster = 0; wCluster < nClusters; wCluster++)
        {
            prob[wCluster] = ((MultiOutput)outputs).getArrayCopy(wCluster);
            Common.sigmoid(prob[wCluster]);
        }

        for (int wSample=0; wSample< problemData.nSamples; wSample++)
        {
            for (int wCluster = 0; wCluster <nClusters; wCluster++)
            {
                prob[wCluster][wSample] += 0.05;
                if (prob[wCluster][wSample] > 1)
                    prob[wCluster][wSample] = 1;
            }
        }

        for (int wSample=0; wSample< problemData.nSamples; wSample++)
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

        if (config.useHits)
        {
            for (int wSample=0; wSample< problemData.nSamples; wSample++)
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
