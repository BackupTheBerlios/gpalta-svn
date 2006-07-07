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

    public void calculate(Output outputs, Individual ind, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        calcProto(outputs, data);

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
                protoError += prob[wClass][wSample] * sampleError;

            }
            //if (protoError == 0)
            //    protoError = Double.MAX_VALUE;
            error += protoError;
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

    private void calcProto(Output outputs, DataHolder data)
    {
        //calculate prototypes for each class:
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            double sumProbThisClass = 0;
            for (int wSample=0; wSample<data.nSamples; wSample++)
            {
                prob[wClass][wSample] = Math.pow(outputs.getArray(wClass)[wSample], m);
                sumProbThisClass += prob[wClass][wSample];
            }

            for (int wVar=0; wVar<data.nVars; wVar++)
            {
                prototypes[wClass][wVar] = 0;
                for (int wSample=0; wSample<data.nSamples; wSample++)
                {
                    prototypes[wClass][wVar] += prob[wClass][wSample] * data.getDataVect(wVar+1)[wSample];
                }
                if (sumProbThisClass!=0)
                    prototypes[wClass][wVar] /= sumProbThisClass;
            }
        }

    }
}
