/*
 * FitnessClustering.java
 *
 * Created on 14 de enero de 2006, 11:44 PM
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
public class FitnessClustering implements Fitness
{
    
    private double[] results;
    private double[][] prototypes;
    private double[] classCenters;
    private Config config;
    private double[][] resp;

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
        if (config.useSoftPertenence)
            resp = new double[config.nClasses][data.nSamples];
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
        
        //see which class each sample belongs to:
        List<Integer>[] clusters = new List[config.nClasses];
        for (int i=0; i<config.nClasses; i++)
            clusters[i] = new ArrayList<Integer>();
        for (int i=0; i<data.nSamples; i++)
        {
            double minDist = Math.abs(results[i] - classCenters[0]);
            int winnerClass = 0;
            for (int j=1; j<config.nClasses; j++)
            {
                double dist = Math.abs(results[i] - classCenters[j]);
                if (dist < minDist)
                {
                    minDist = dist;
                    winnerClass = j;
                }
            }
            clusters[winnerClass].add(i);
        }
        
        //calculate prototypes for each class:
        for (int i=0; i<config.nClasses; i++)
        {
            for (int j=0; j<data.nVars; j++)
            {
                prototypes[i][j] = 0;
                for (int sample : clusters[i])
                    prototypes[i][j] += data.getDataVect(j+1)[sample];
                prototypes[i][j] /= clusters[i].size();
            }
        }
        
        //calculate total error:
        double error = 0;
        for (int i=0; i<config.nClasses; i++)
        {
            double protoError = 0;
            for (int sample : clusters[i])
            {
                double sampleError = 0;
                for (int j=0; j<data.nVars; j++)
                    sampleError += Math.pow(prototypes[i][j] - data.getDataVect(j+1)[sample], 2);
                protoError += Math.sqrt(sampleError);
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
