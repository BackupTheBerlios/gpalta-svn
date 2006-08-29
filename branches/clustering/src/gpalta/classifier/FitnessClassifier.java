package gpalta.classifier;

import gpalta.core.*;
import gpalta.clustering.BufferedTree;
import gpalta.clustering.TreeGroup;
import gpalta.clustering.ClusteringOutput;
import gpalta.clustering.FitnessGroup;

/**
 * Created by IntelliJ IDEA. User: nvn Date: 24-08-2006 Time: 12:36:39 PM To change this template
 * use File | Settings | File Templates.
 */
public class FitnessClassifier extends FitnessGroup
{
    private double[][] prob;
    private double[][] pReal;
    private Config config;
    public void init(Config config, DataHolder data, String fileName)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void init(Config config, DataHolder data, Output desiredOutputs, double[] weights)
    {
        this.config = config;
        prob = new double[config.nClasses][data.nSamples];
        pReal = new double[config.nClasses][data.nSamples];
        double[] tmp = desiredOutputs.getArray(0);
        /*
        for (int i=0; i<pReal.length; i++)
            for (int j=0;j<pReal[0].length; j++)
                pReal[i][j] = 0.01;
        */
        for (int wSample=0; wSample<data.nSamples; wSample++)
            pReal[(int)tmp[wSample]-1][wSample] = 1;
    }

    public void calculate(Output outputs, Individual ind, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        double[] ce = new double[config.nClasses];
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            prob[wClass] = outputs.getArray(wClass);
        }
        /*
        for (int wSample=0; wSample<data.nSamples; wSample++)
        {
            double sum = 0;
            for (int wClass=0; wClass<config.nClasses; wClass++)
                sum += prob[wClass][wSample];
            for (int wClass=0; wClass<config.nClasses; wClass++)
                prob[wClass][wSample] /= sum;
        }
        */
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            ce[wClass] = crossEntropy(pReal[wClass], prob[wClass]);
            //ce[wClass] = crossEntropy(prob[wClass], pReal[wClass]);
        }
        ind.setFitness(1 / (1+ Common.sum(ce)/config.nClasses));
        for (int wClass=0; wClass<config.nClasses; wClass++)
            ce[wClass] = 1 / (1 + ce[wClass]);
        assignFitness(ind, 1 / (1+ Common.sum(ce)/config.nClasses), ce, config);
    }

    public Output getProcessedOutput(Output raw, Individual ind, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        ClusteringOutput processed = new ClusteringOutput(config.nClasses, data.nSamples);
        for (int i = 0; i < config.nClasses; i++)
        {
            processed.setArray(i, raw.getArrayCopy(i));
            prob[i] = raw.getArray(i);
        }
        processed.setPertenenceCopy(prob);
        return processed;
    }

    private double crossEntropy(double[] pReal, double[] pEstimated)
    {
        double out = 0;
        for (int i=0; i<pReal.length; i++)
        {
            out += Math.pow(pReal[i] - pEstimated[i],2);
            //out += pReal[i] * Math.log(1 / pEstimated[i]);
            /*
            if (Math.abs(pReal[i] - pEstimated[i]) > 0.001)
                out += pReal[i] * Math.log(pReal[i] / pEstimated[i]);
            */
        }
        //return out / Math.log(2);
        return out;
    }
}
