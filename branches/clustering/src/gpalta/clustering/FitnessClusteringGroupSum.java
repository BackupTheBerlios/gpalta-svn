package gpalta.clustering;

import gpalta.core.*;

/**
 * Created by IntelliJ IDEA.
 * User: neven
 * Date: 11-07-2006
 * Time: 05:58:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class FitnessClusteringGroupSum extends FitnessClusteringGroup
{
    private double[][] d;
    public void calculate(Output outputs, Individual ind, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        for (int wSample = 0; wSample < data.nSamples; wSample++)
            for (int wClass = 0; wClass < config.nClasses; wClass++)
                prob[wClass][wSample] = outputs.getArray(wClass)[wSample];
        Common.maxPerColInline(prob);

        double error = 0;
        for (int wClass = 0; wClass < config.nClasses; wClass++)
        {
            double nThisClass = Common.sum(prob[wClass]);
            double errorThisClass = 0;
            for (int s1=0; s1 < data.nSamples-1; s1++)
            {
                if (prob[wClass][s1] == 1)
                {
                    for (int s2=s1+1; s2<data.nSamples; s2++)
                    {
                        if (prob[wClass][s2] == 1)
                            errorThisClass += d[s1][s2];
                    }
                }
            }
            error += errorThisClass/nThisClass;
        }

        double fitness = 1 / (1 + error);
        ind.setFitness(fitness);
        Tree t;
        for (int i = 0; i < config.nClasses; i++)
        {
            t = ((TreeGroup) ind).getTree(i);
            if (fitness > t.readFitness())
                t.setFitness(penalizedFitness(fitness, t.getMaxDepthFromHere()));
        }
    }

    public Output getProcessedOutput(Output raw, Individual ind, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        return raw;
    }

    public void init(Config config, DataHolder data, Output desiredOutputs, double[] weights)
    {
        super.init(config, data, desiredOutputs, weights);

        d = new double[data.nSamples][data.nSamples];

        double[] x1 = new double[data.nVars];
        double[] x2 = new double[data.nVars];
        for (int s1=0; s1 < data.nSamples-1; s1++)
        {
            for (int wVar = 0; wVar < data.nVars; wVar++)
                x1[wVar] = data.getDataVect(wVar+1)[s1];
            for (int s2=s1+1; s2<data.nSamples; s2++)
            {
                for (int wVar = 0; wVar < data.nVars; wVar++)
                    x2[wVar] = data.getDataVect(wVar+1)[s2];
                d[s1][s2] += Common.dist2(x1, x2);
            }
        }
    }
}
