/*
 * NewClass.java
 *
 * Created on 8 de junio de 2006, 06:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gpalta.clustering;
import gpalta.core.*;

/**
 *
 * @author neven
 */
public class FitnessClusteringGroup implements Fitness
{
    public double[][] prototypes;
    private Config config;
    private double m = 2;
    
    /** Creates a new instance of NewClass */
    public FitnessClusteringGroup()
    {
    }

    public void init(Config config, DataHolder data, String fileName)
    {
        init(config, data, null, null);
    }

    public void init(Config config, DataHolder data, Output desiredOutputs, double[] weights)
    {
        this.config = config;
        prototypes = new double[config.nClasses][data.nVars];
    }

    public void calculate(Output outputs, Individual ind, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        
        //calculate prototypes for each class:
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            double sumProbThisClass = 0;
            for (int wSample=0; wSample<data.nSamples; wSample++)
                sumProbThisClass += Math.pow(outputs.getArray(wClass)[wSample], m);
            
            for (int wVar=0; wVar<data.nVars; wVar++)
            {
                prototypes[wClass][wVar] = 0;
                for (int wSample=0; wSample<data.nSamples; wSample++)
                {
                    prototypes[wClass][wVar] += Math.pow(outputs.getArray(wClass)[wSample], m) * data.getDataVect(wVar+1)[wSample];
                }
                prototypes[wClass][wVar] /= sumProbThisClass;
            }
        }
        
        //calculate total error:
        double error = 0;
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            double protoError = 0;
            for (int wSample=0; wSample<data.nSamples; wSample++)
            {
                double sampleError = 0;
                for (int wVar=0; wVar<data.nVars; wVar++)
                    sampleError += Math.pow(prototypes[wClass][wVar] - data.getDataVect(wVar+1)[wSample], 2);
                protoError += Math.pow(outputs.getArray(wClass)[wSample], m) * sampleError;
            }
            error += protoError;
        }
        double fitness = 1/(1 + error);
        ind.setFitness(fitness);
        for (int i=0; i<config.nClasses; i++)
        {
            if (fitness > ((TreeGroup)ind).get(i).readFitness())
                ((TreeGroup)ind).get(i).setFitness(fitness);
        }
    }
    
    public Output getProcessedOutput(Output raw, Individual ind, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        ClusteringOutput processed = new ClusteringOutput(config.nClasses, data.nSamples);
        for (int i=0; i<config.nClasses; i++)
        {
            processed.setArray(i, raw.getArrayCopy(i));
        }
        processed.setPrototypesCopy(prototypes);
        return processed;
    }
    
}
