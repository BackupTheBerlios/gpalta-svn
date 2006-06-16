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
 *
 * @author neven
 */
public class FitnessClusteringGroup implements Fitness
{
    private double[][] prototypes;
    private double[][] prob;
    private Config config;
    private double m;
    
    public void init(Config config, DataHolder data, String fileName)
    {
        init(config, data, null, null);
    }

    public void init(Config config, DataHolder data, Output desiredOutputs, double[] weights)
    {
        this.config = config;
        this.m = config.m;
        prototypes = new double[config.nClasses][data.nVars];
        prob = new double[config.nClasses][data.nSamples];
    }

    public void calculate(Output outputs, Individual ind, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        calcProto(outputs, data);
        
        //calculate total error:
        double error = 0;
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            double protoError = 0;
            for (int wSample=0; wSample<data.nSamples; wSample++)
            {
                double sampleError = 0;
                if (prob[wClass][wSample] != 0)
                {
                    for (int wVar=0; wVar<data.nVars; wVar++)
                        sampleError += Math.pow(prototypes[wClass][wVar] - data.getDataVect(wVar+1)[wSample], 2);
                    protoError += prob[wClass][wSample] * Math.sqrt(sampleError);
                }
            }
            if (protoError == 0)
                protoError = Double.MAX_VALUE;
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
    
    private void calcProto(Output outputs, DataHolder data)
    {
        //calculate prototypes for each class:
        /*for (int wClass=0; wClass<config.nClasses; wClass++)
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
        }*/
        
        for (int wSample=0; wSample<data.nSamples; wSample++)
        {
            double maxProb = 0;
            int winner = 0;
            double p;
            for (int wClass=0; wClass<config.nClasses; wClass++)
            {
                prob[wClass][wSample] = 0;
                if ((p=outputs.getArray(wClass)[wSample]) > maxProb)
                {
                    maxProb = p;
                    winner = wClass;
                }
            }
            prob[winner][wSample] = 1;
        }
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            double sumProbThisClass = 0;
            for (int wSample=0; wSample<data.nSamples; wSample++)
            {
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
    
    public Output getProcessedOutput(Output raw, Individual ind, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        ClusteringOutput processed = new ClusteringOutput(config.nClasses, data.nSamples);
        for (int i=0; i<config.nClasses; i++)
        {
            processed.setArray(i, raw.getArrayCopy(i));
        }
        calcProto(raw, data);
        processed.setPrototypesCopy(prototypes);
        return processed;
    }
    
}
