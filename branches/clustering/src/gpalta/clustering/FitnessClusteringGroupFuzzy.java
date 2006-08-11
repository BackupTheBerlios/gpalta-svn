package gpalta.clustering;

import gpalta.core.*;

/**
 * Created by IntelliJ IDEA.
 * User: neven
 * Date: 07-07-2006
 * Time: 05:24:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class FitnessClusteringGroupFuzzy extends FitnessClusteringGroup
{

    private double[][] prob2;

    public void calculate(Output outputs, Individual ind, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        calcProto(outputs, data);

        for (int wSample=0; wSample<data.nSamples; wSample++)
        {
            for (int wClass = 0; wClass < config.nClasses; wClass++)
            {
                prob2[wSample][wClass] = 0;
                for (int wVar = 0; wVar < data.nVars; wVar++)
                    prob2[wSample][wClass] += Math.pow(prototypes[wClass][wVar] - data.getDataVect(wVar + 1)[wSample], 2);
            }
            double sumProb = Common.sum(prob2[wSample]);
            for (int wClass = 0; wClass < config.nClasses; wClass++)
                prob2[wSample][wClass] /= sumProb;

        }

        //calculate total error:
        double error = 0;
        for (int wClass = 0; wClass < config.nClasses; wClass++)
        {
            double protoError = 0;
            for (int wSample = 0; wSample < data.nSamples; wSample++)
            {
                double sampleError = 0;
                for (int wVar = 0; wVar < data.nVars; wVar++)
                    sampleError += Math.pow(prototypes[wClass][wVar] - data.getDataVect(wVar + 1)[wSample], 2);
                protoError += prob2[wSample][wClass] * sampleError;

            }
            error += protoError;
        }

        /*double[][] maxProb = Common.copy(pxy);
        Common.maxPerColInline(maxProb);
        for (int wClass = 0; wClass < config.nClasses; wClass++)
        {
            if (Common.sum(maxProb[wClass]) < 1)
            {
                error = Double.MAX_VALUE;
                break;
            }
            if (Common.sum(pxy[wClass]) < 0.1*data.nSamples)
            {
                error = Double.MAX_VALUE;
                break;
            }
        }*/

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

    /*private void calcProto(Output outputs, DataHolder data)
    {
        //calculate prototypes for each class:
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            double sumProbThisClass = 0;
            for (int wSample=0; wSample<data.nSamples; wSample++)
            {
                pxy[wClass][wSample] = Math.pow(outputs.getArray(wClass)[wSample], m);
                sumProbThisClass += pxy[wClass][wSample];
            }

            for (int wVar=0; wVar<data.nVars; wVar++)
            {
                prototypes[wClass][wVar] = 0;
                for (int wSample=0; wSample<data.nSamples; wSample++)
                {
                    prototypes[wClass][wVar] += pxy[wClass][wSample] * data.getDataVect(wVar+1)[wSample];
                }
                if (sumProbThisClass!=0)
                    prototypes[wClass][wVar] /= sumProbThisClass;
            }
        }

    }*/

    public void init(Config config, DataHolder data, Output desiredOutputs, double[] weights)
    {
        super.init(config, data, desiredOutputs, weights);
        prob2 = new double[data.nSamples][config.nClasses];
    }

}
