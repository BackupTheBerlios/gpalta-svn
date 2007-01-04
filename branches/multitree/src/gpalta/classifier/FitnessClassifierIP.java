package gpalta.classifier;

import gpalta.clustering.FitnessGroup;
import gpalta.clustering.ClusteringOutput;
import gpalta.core.*;

/**
 * Created by IntelliJ IDEA. User: nvn Date: 24-08-2006 Time: 12:36:39 PM To change this template
 * use File | Settings | File Templates.
 */
public class FitnessClassifierIP extends FitnessGroup
{
    private double[][] prob;
    private double[][] pReal;
    private double[] pcGlobal;
    private Config config;
    private double[] labels;
    private double[] error;
    public void init(Config config, DataHolder data, String fileName)
    {
    }

    public void init(Config config, DataHolder data, Output desiredOutputs, double[] weights)
    {
        this.config = config;
        prob = new double[config.nClasses][];
        pReal = new double[config.nClasses][data.nSamples];
        labels = desiredOutputs.getArray(0);

        double[] possibleLabels = new double[config.nClasses];
        for (int i=0; i<config.nClasses; i++)
            possibleLabels[i] = i+1;
        pcGlobal = InformationTheory.px(labels, possibleLabels);

        for (int wSample=0; wSample<data.nSamples; wSample++)
            pReal[(int)labels[wSample]-1][wSample] = 1;

        error = new double[data.nSamples];
    }

    public void calculate(Output outputs, Individual ind, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        double[] ce = new double[config.nClasses];
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            prob[wClass] = outputs.getArrayCopy(wClass);
        }

        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            for (int wSample=0; wSample<data.nSamples; wSample++)
            {
                error[wSample] = prob[wClass][wSample] - pReal[wClass][wSample];
            }
            ce[wClass] = InformationTheory.informationPotencial(error, 1);
        }

        double[] winner = new double[data.nSamples];
        for (int wSample=0; wSample<data.nSamples; wSample++)
        {
            double max = prob[0][wSample];
            winner[wSample] = 1;
            for (int wClass=1; wClass<config.nClasses; wClass++)
            {
                if (prob[wClass][wSample] > max)
                {
                    max = prob[wClass][wSample];
                    winner[wSample] = wClass+1;
                }
            }
        }

        double[] possibleLabels = new double[config.nClasses];
        for (int i=0; i<config.nClasses; i++)
            possibleLabels[i] = i+1;

        double[] px = InformationTheory.px(winner, possibleLabels);
        double[][] pxy = InformationTheory.pxy(winner, labels, possibleLabels);
        double err = InformationTheory.mutualInformation(px, pcGlobal, pxy);

        assignFitness(ind, err , ce, config);
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

}
