package gpalta.classifier;

import gpalta.clustering.FitnessGroup;
import gpalta.clustering.ClusteringOutput;
import gpalta.core.*;

/**
 * Created by IntelliJ IDEA. User: nvn Date: 24-08-2006 Time: 12:36:39 PM To change this template
 * use File | Settings | File Templates.
 */
public class FitnessClassifierIT extends FitnessGroup
{
    private double[][] prob;
    private double[][] pReal;
    private double[] pcGlobal;
    private double[][] pc;
    private Config config;
    private double[] labels;
    public void init(Config config, DataHolder data, String fileName)
    {
    }

    public void init(Config config, DataHolder data, Output desiredOutputs, double[] weights)
    {
        this.config = config;
        prob = new double[config.nClasses][];
        pReal = new double[config.nClasses][data.nSamples];
        labels = desiredOutputs.getArray(0);

        double[] possibleValues = new double[config.nClasses];
        for (int i=0; i<config.nClasses; i++)
            possibleValues[i] = i+1;

        for (int wSample=0; wSample<data.nSamples; wSample++)
            pReal[(int)labels[wSample]-1][wSample] = 1;

        pcGlobal = InformationTheory.px(labels, possibleValues);

        pc = new double[config.nClasses][2];
        double[] possibleLabels = {0,1};
        for (int i=0; i<config.nClasses; i++)
        {
            pc[i] = InformationTheory.px(pReal[i], possibleLabels);
        }
    }

    public void calculate(Output outputs, Individual ind, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        double[] ce = new double[config.nClasses];
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            prob[wClass] = outputs.getArrayCopy(wClass);
        }

        Common.maxPerColInline(prob);

        double[][] pce = new double[config.nClasses][];
        double[] possibleLabels = {0,1};
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            double[] px = InformationTheory.px(prob[wClass], possibleLabels);
            double[][] pxy = InformationTheory.pxy(prob[wClass], pReal[wClass], possibleLabels);
            ce[wClass] = InformationTheory.mutualInformation(px, pc[wClass], pxy);
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

        possibleLabels = new double[config.nClasses];
        for (int i=0; i<config.nClasses; i++)
            possibleLabels[i] = i+1;

        double[] px = InformationTheory.px(winner, possibleLabels);
        double[][] pxy = InformationTheory.pxy(winner, labels, possibleLabels);
        double fit = InformationTheory.mutualInformation(px, pcGlobal, pxy);

        assignFitness(ind, fit , ce, config);
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
