/*
 * FitnessClusteringFuzzy.java
 *
 * Created on 21 de abril de 2006, 03:06 PM
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

package gpalta.core;

import gpalta.nodes.*;
import java.util.*;

/**
 *
 * @author neven
 */
public class FitnessClusteringFuzzy implements Fitness
{
    
    private double[] results;
    public double[][] prototypes;
    public double[] classCenters;
    private Config config;
    public double[][] prob;
    private double m = 2;

    public void init(Config config, DataHolder data, String fileName)
    {
        init(config, data, null, null);
    }

    public void init(Config config, DataHolder data, double[] desiredOutputs, double[] weights)
    {
        this.config = config;
        results = new double[data.nSamples];
        prototypes = new double[config.nClasses][data.nVars];
        classCenters = new double[config.nClasses];
        for (int i=0; i<config.nClasses; i++)
            classCenters[i] = config.scale * (-1 + 1/(double)config.nClasses + 2*(double)i/(config.nClasses));
        prob = new double[config.nClasses][data.nSamples];
        this.m = m;
    }

    public double[] calculate(Tree tree, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        data.reset();
        if (config.nPreviousOutput == 0 && config.useVect)
        {
            tree.evalVect(results, evalVectors, data, prev);
        }
        else
        {
            for (int i=0; i<data.nSamples; i++)
            {
                results[i] = tree.eval(data, prev);
                data.update();
            }
        }
        
        for (int i=0; i<data.nSamples; i++)
            results[i] = config.scale * Math.tanh(results[i]);
        
        //assign pertenence probabilities (U matrix) without normalizing (possibilistic)
        for (int wSample=0; wSample<data.nSamples; wSample++)
            for (int wClass=0; wClass<config.nClasses; wClass++)
                //prob[wClass][wSample] = 1 / (1 + Math.pow(config.scale * Math.abs(classCenters[wClass] - results[wSample]), m));
                prob[wClass][wSample] = Math.exp(-4*Math.pow(classCenters[wClass] - results[wSample], 2));
        
        //calculate prototypes for each class:
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            double sumProbThisClass = 0;
            for (int wSample=0; wSample<data.nSamples; wSample++)
                sumProbThisClass += Math.pow(prob[wClass][wSample], m);
            
            for (int wVar=0; wVar<data.nVars; wVar++)
            {
                prototypes[wClass][wVar] = 0;
                for (int wSample=0; wSample<data.nSamples; wSample++)
                {
                    prototypes[wClass][wVar] += Math.pow(prob[wClass][wSample], m) * data.getDataVect(wVar+1)[wSample];
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
                protoError += Math.pow(prob[wClass][wSample], m) * sampleError;
            }
            error += protoError;
        }
        tree.fitness = 1/(1 + error);
        
        if (config.outputPrototypes)
        {
            double[] out = new double[data.nVars * config.nClasses];
            for (int i=0; i<config.nClasses; i++)
                for (int j=0; j<data.nVars; j++)
                    out[i*data.nVars + j] = prototypes[i][j];

            return out;
        }
        return results;
        
    }
    
}
