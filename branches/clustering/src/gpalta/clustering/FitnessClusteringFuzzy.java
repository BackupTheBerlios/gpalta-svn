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

package gpalta.clustering;

import gpalta.core.*;

/**
 * @author neven
 */
public class FitnessClusteringFuzzy implements Fitness
{

    public double[][] prototypes;
    public double[] classCenters;
    private Config config;
    public double[][] prob;
    private double m;
    private double[] results;

    public void init(Config config, DataHolder data, String fileName)
    {
        init(config, data, null, null);
    }

    public void init(Config config, DataHolder data, Output desiredOutputs, double[] weights)
    {
        this.config = config;
        prototypes = new double[config.nClasses][data.nVars];
        classCenters = new double[config.nClasses];
        for (int i = 0; i < config.nClasses; i++)
            classCenters[i] = config.scale * (-1 + 1 / (double) config.nClasses + 2 * (double) i / (config.nClasses));
        prob = new double[config.nClasses][data.nSamples];
        this.m = m;
        results = new double[data.nSamples];
    }

    public void calculate(Output outputs, Individual ind, TempOutputFactory tempOutputFactory, DataHolder data)
    {

        for (int i = 0; i < data.nSamples; i++)
            results[i] = config.scale * Math.tanh(outputs.getArray(0)[i]);

        //assign pertenence probabilities (U matrix) without normalizing (possibilistic)
        for (int wSample = 0; wSample < data.nSamples; wSample++)
            for (int wClass = 0; wClass < config.nClasses; wClass++)
                //pxy[wClass][wSample] = 1 / (1 + Math.pow(config.scale * Math.abs(classCenters[wClass] - results[wSample]), m));
                prob[wClass][wSample] = Math.exp(-4 * Math.pow(classCenters[wClass] - results[wSample], 2));

        //calculate prototypes for each class:
        for (int wClass = 0; wClass < config.nClasses; wClass++)
        {
            double sumProbThisClass = 0;
            for (int wSample = 0; wSample < data.nSamples; wSample++)
                sumProbThisClass += Math.pow(prob[wClass][wSample], m);

            for (int wVar = 0; wVar < data.nVars; wVar++)
            {
                prototypes[wClass][wVar] = 0;
                for (int wSample = 0; wSample < data.nSamples; wSample++)
                {
                    prototypes[wClass][wVar] += Math.pow(prob[wClass][wSample], m) * data.getDataVect(wVar + 1)[wSample];
                }
                if (sumProbThisClass != 0)
                    prototypes[wClass][wVar] /= sumProbThisClass;
            }
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
                protoError += Math.pow(prob[wClass][wSample], m) * sampleError;
            }
            error += protoError;
        }
        ind.setFitness(1 / (1 + error));

    }

    public Output getProcessedOutput(Output raw, Individual ind, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        ClusteringOutput processed = new ClusteringOutput(1, data.nSamples);
        processed.setArray(0, raw.getArrayCopy(0));
        processed.setPertenenceCopy(prob);
        processed.setPrototypesCopy(prototypes);
        return processed;
    }

}
