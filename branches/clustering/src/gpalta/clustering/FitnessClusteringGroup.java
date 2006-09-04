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
 * @author neven
 */
public class FitnessClusteringGroup extends FitnessGroup
{
    protected double[][] prototypes;
    protected double[][] prob;
    protected Config config;

    public void init(Config config, DataHolder data, String fileName)
    {
        init(config, data, null, null);
    }

    public void init(Config config, DataHolder data, Output desiredOutputs, double[] weights)
    {
        this.config = config;
        prototypes = new double[config.nClasses][data.nVars];
        prob = new double[config.nClasses][data.nSamples];
    }

    public void calculate(Output outputs, Individual ind, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        calcProto(outputs, data);

        //calculate total error:
        double error = 0;
        double[] x;
        double[] protoError = new double[config.nClasses];
        int[] nEachClass = new int[config.nClasses];
        for (int wClass = 0; wClass < config.nClasses; wClass++)
        {
            protoError[wClass] = 0;
            nEachClass[wClass] = 0;
            for (int wSample = 0; wSample < data.nSamples; wSample++)
            {
                if (prob[wClass][wSample] != 0)
                {
                    x = data.getAllVars(wSample);
                    //protoError[wClass] += Common.dist2(prototypes[wClass], x);
                    nEachClass[wClass]++;

                    double sampleError = 0;
                    for (int wVar=0; wVar<data.nVars; wVar++)
                        sampleError += Math.abs(prototypes[wClass][wVar]-x[wVar])/data.getRange(wVar+1);
                    protoError[wClass] += sampleError/data.nVars;
                }
            }
            error += protoError[wClass];
            protoError[wClass] = 1 / (1 + protoError[wClass]/nEachClass[wClass]);
            //protoError[wClass] = 1 / (1 + protoError[wClass]);
        }
        error /= config.nClasses;
        assignFitness(ind, 1 / (1 + error), protoError, config);
    }

    protected void calcProto(Output outputs, DataHolder data)
    {
        for (int wClass = 0; wClass < config.nClasses; wClass++)
            System.arraycopy(outputs.getArray(wClass), 0, prob[wClass], 0, data.nSamples);
        Common.maxPerColInline(prob);

        double[] x;
        for (int wClass = 0; wClass < config.nClasses; wClass++)
        {
            double sumProbThisClass = Common.sum(prob[wClass]);

            for (int wVar = 0; wVar < data.nVars; wVar++)
            {
                prototypes[wClass][wVar] = 0;
            }
            if (sumProbThisClass != 0)
            {
                for (int wSample = 0; wSample < data.nSamples; wSample++)
                {
                    if (prob[wClass][wSample] != 0)
                    {
                        x = data.getAllVars(wSample);
                        for (int wVar = 0; wVar < data.nVars; wVar++)
                        {
                            prototypes[wClass][wVar] += x[wVar];
                        }
                    }
                }
                for (int wVar = 0; wVar < data.nVars; wVar++)
                {
                    prototypes[wClass][wVar] /= sumProbThisClass;
                }
            }
        }
    }

    public Output getProcessedOutput(Output raw, Individual ind, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        ClusteringOutput processed = new ClusteringOutput(config.nClasses, data.nSamples);
        for (int i = 0; i < config.nClasses; i++)
        {
            processed.setArray(i, raw.getArrayCopy(i));
        }
        calcProto(raw, data);
        processed.setPrototypesCopy(prototypes);
        processed.setPertenenceCopy(prob);
        return processed;
    }

}
