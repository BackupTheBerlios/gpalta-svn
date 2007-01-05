package gpalta.clustering;

import gpalta.core.*;
import gpalta.multitree.MultiOutput;

/**
 * @author neven
 */
public class FitnessClusteringCS implements Fitness
{
    private double[][] kernel;
    private double[][] prob;
    private Config config;
    private int[] target;

    public void init(Config config, DataHolder data, String fileName)
    {
        init(config, data, null, null);
    }

    public void init(Config config, DataHolder data, Output desiredOutputs, double[] weights)
    {
        this.config = config;

        double d = data.nVars;
        double n = data.nSamples;
        double sigmax2 = 0;
        for (int i=0; i<data.nVars; i++)
        {
            sigmax2 += Common.variance(data.getDataVect(i+1));
        }
        sigmax2 /= d;

        double sigmaOpt2 = sigmax2*Math.pow(4/(n*(2*d+1)), 2/(d + 4));

        kernel = new double[data.nSamples][data.nSamples];
        double[] dx = new double[data.nVars];
        double[] x1;
        double[] x2;
        for (int i=0; i<data.nSamples; i++)
        {
            x1 = data.getSample(i);
            for (int j=0; j<data.nSamples; j++)
            {
                x2 = data.getSample(j);
                for (int wVar=0; wVar<data.nVars; wVar++)
                {
                    dx[wVar] = x1[wVar] - x2[wVar];
                }
                kernel[i][j] = InformationTheory.gaussianKernel(dx, 2*sigmaOpt2/1.5);
                //kernel[i][j] = InformationTheory.gaussianKernel(dx, .01);
            }
        }
        target = new int[data.nSamples];
        for (int i=0; i<data.nSamples; i++)
        {
            target[i] = (int)((SingleOutput)desiredOutputs).x[i];
        }

    }

    public void calculate(Output outputs, Individual ind, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        calcProto(outputs, data);

        int nClusters = outputs.getDim();
        double[] cInfo = new double[nClusters];

        int[] samples = Common.randPerm(data.nSamples);

        int nSubSamples = (int)(0.3*data.nSamples);

        for (int wCluster=0; wCluster<nClusters; wCluster++)
        {
            for (int i=0; i<nSubSamples; i++)
            {
                for (int j=0; j<data.nSamples; j++)
                {
                    cInfo[wCluster] += prob[wCluster][samples[i]]*prob[wCluster][j]*kernel[samples[i]][j];
                }
            }
        }

        double info = 0;

        double[][] probT = Common.transpose(prob);

        for (int i=0; i<nSubSamples; i++)
        {
            for (int j=0; j<data.nSamples; j++)
            {
                info += (1-Common.dotProduct(probT[samples[i]], probT[j]))*kernel[samples[i]][j];
            }
        }

        for (int wCluster = 0; wCluster < nClusters; wCluster++)
        {
            info /= Math.sqrt(cInfo[wCluster]);
        }
        info /= 2;

        ind.setFitness(-info);
    }

    protected void calcProto(Output outputs, DataHolder data)
    {
        int nClusters = outputs.getDim();
        prob = new double[nClusters][];
        for (int wCluster = 0; wCluster < nClusters; wCluster++)
        {
            prob[wCluster] = ((MultiOutput)outputs).getArrayCopy(wCluster);
            Common.sigmoid(prob[wCluster]);
        }

        for (int wSample=0; wSample< data.nSamples; wSample++)
        {
            for (int wCluster = 0; wCluster <nClusters; wCluster++)
            {
                prob[wCluster][wSample] += 0.05;
                if (prob[wCluster][wSample] > 1)
                            prob[wCluster][wSample] = 1;
            }
        }

        for (int wSample=0; wSample< data.nSamples; wSample++)
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

    public Output getProcessedOutput(Output raw, Individual ind, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        int nClusters = raw.getDim();
        ClusteringOutput processed = new ClusteringOutput(nClusters, data.nSamples);
        for (int i = 0; i < nClusters; i++)
        {
            processed.store(i, ((MultiOutput)raw).getArrayCopy(i));
        }
        calcProto(raw, data);
        processed.setPertenenceCopy(prob);
        return processed;
    }

    public int hits(int[] system, int[] real, int nClasses)
    {
        int hits = 0;
        int[][] assigned = new int[nClasses][nClasses];
        for (int i=0; i<system.length; i++)
        {
            if (real[i] >= nClasses)
                continue;
            assigned[real[i]][system[i]]++;
        }
        boolean done = false;
        int[] eq = new int[nClasses];
        int[] eq2 = new int[nClasses];
        for (int i=0; i<nClasses; i++)
        {
            eq[i] = -1;
            eq2[i] = -1;
        }
        while(!done)
        {

            for (int rc=0; rc<nClasses; rc++)
            {
                if (eq[rc] != -1)
                    continue;
                int max = -1;
                int winner = 0;
                for (int sc=0; sc<nClasses; sc++)
                {
                    if (eq2[sc] != -1)
                        continue;
                    if(assigned[rc][sc] > max)
                    {
                        max = assigned[rc][sc];
                        winner = sc;
                    }
                }
                int winner2 = rc;
                for (int rc2=0; rc2<nClasses; rc2++)
                {
                    if (eq[rc2] != -1)
                        continue;
                    if(assigned[rc2][winner] > max)
                    {
                        max = assigned[rc2][winner];
                        winner2 = rc2;
                    }
                }
                if (winner2 == rc)
                {
                    eq[rc] = winner;
                    eq2[winner] = rc;
                }
            }
            done = true;
            for (int i=0; i<nClasses; i++)
            {
                if (eq[i]==-1)
                {
                    done = false;
                    break;
                }
            }
        }
        for (int i=0; i<system.length; i++)
        {
            if (real[i] >= nClasses)
                continue;
            if (system[i] == eq[real[i]])
            {
                hits++;
            }
        }
        return hits;
    }

}
