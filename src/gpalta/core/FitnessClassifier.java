/*
 * FitnessClassifier.java
 *
 * Created on 13 de octubre de 2005, 03:37 PM
 *
 * Copyright (C) 2005  Neven Boric <nboric@gmail.com>
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
import gpalta.ops.*;
import java.util.*;
import java.io.*;

/**
 * Fitness suitable for classifiers
 * @author DSP
 */
public class FitnessClassifier implements Fitness
{
    
    private double[] desiredOutputs;
    private double[] weights;
    private int n0;
    private int n1;
    private double sizePenalization;
    private boolean useWeight;
    private double deltaSNR;
    private double kHR1;
    private double continuityImportance;
    private double[] results;
    private Config config;
    
    /** 
     * Initializes only constants (to avoid duplicating code)
     */
    private void init (Config config, DataHolder data)
    {
        this.config = config;
        /* How much each SNR is more important than the next one:
         * Must be smaller than 1/3
         */
        deltaSNR = config.deltaSNR;
        continuityImportance = config.continuityImportance;
        //How much important is voice over silence:
        kHR1 = config.kHR1;
        
        sizePenalization = 1/ (500*Math.pow(2, config.maxDepth+1));
        
        results = new double[data.nSamples];
    }
    
    public void init(Config config, DataHolder data, String fileName)
    {
        // only init constants:
        init(config, data);

        File classFile = new File(fileName);

        desiredOutputs = new double[data.nSamples];
        n0 = 0;
        n1 = 0;

        try
        {
            BufferedReader out = new BufferedReader(new FileReader(classFile));

            //Find out if we are using snr info:
            useWeight = false;
            String line = out.readLine().trim();
            if (line.split("\\s+").length == 2)
            {
                useWeight = true;
                weights = new double[data.nSamples];
            }
            out = new BufferedReader(new FileReader(classFile));


            for (int sample=0; sample<data.nSamples; sample++)
            {
                line = out.readLine().trim();
                if (useWeight)
                {
                    String[] vals = line.split("\\s+");
                    //First comes the desiredOutput:
                    desiredOutputs[sample] = Double.parseDouble(vals[0]);
                    //Then the weight:
                    weights[sample] = Double.parseDouble(vals[1]);
                }
                else
                {
                    desiredOutputs[sample] = Double.parseDouble(line);
                }
                if (desiredOutputs[sample] == 0)
                {
                    n0++;
                }
                else
                {
                    n1++;
                }
            }
            
            Logger.log("Using classifier fitness");
            Logger.log("Fitness initialized from file \"" + fileName + "\"");
            Logger.log("\t kHR1:                " + kHR1);
            if (useWeight)
            {
                Logger.log("\t Using SNR data");
                Logger.log("\t deltaSNR:            " + deltaSNR);
            }
            Logger.log("\t Frames:              " + (n0+n1));
            Logger.log("\t N0:                  " + n0);
            Logger.log("\t N1:                  " + n1);

        }
        /* TODO: These exceptions shouldn't be catched here, but thrown to the
         * evolution and then to the controller
         */
        catch (FileNotFoundException e)
        {
            Logger.log(e);
        }
        catch (IOException e)
        {
            Logger.log(e);
        }
        catch (NumberFormatException e)
        {
            Logger.log(e);
        }
    }
    
    public void init (Config config, DataHolder data, double[] desiredOutputs, double[] weights)
    {
        // only init constants:
        init(config, data);
        
        this.desiredOutputs = desiredOutputs;
        this.weights = weights;
        for (int sample=0; sample<data.nSamples; sample++)
        {
            if (desiredOutputs[sample] == 0)
            {
                n0++;
            }
            else
            {
                n1++;
            }
        }
    }
    
    public double[] calculate(Tree tree, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        
        if (!tree.fitCalculated)
        {
            data.reset();
            prev.reset();
            double hits0 = 0;
            double hits1 = 0;
            int maxContinuity = 0;
            int sumMaxContinuity = 0;
            double previousTarget = 0;
            int continuity = 0;
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
                    prev.update(results[i]);
                }
            }
            for (int i=0; i<data.nSamples; i++)
            {
                if (desiredOutputs[i]!=previousTarget)
                {
                    previousTarget = desiredOutputs[i];
                    sumMaxContinuity += maxContinuity;
                    maxContinuity= 0;
                }
                if (results[i]!=0 && desiredOutputs[i]!=0)
                {
                    if (useWeight)
                    {
                        /* For each SNR, the importance is:
                         * clean: 1 + 3*deltaSNR
                         * 20 dB: 1 + 2*deltaSNR
                         * 15 dB: 1 + 1*deltaSNR
                         * 10 dB: 1 + 0*deltaSNR
                         *  5 dB: 1 - 1*deltaSNR
                         *  0 dB: 1 - 2*deltaSNR
                         * -5 dB: 1 - 3*deltaSNR
                         * This works if snrs come specified in this order
                         * form 0 to 6 in the class file
                         */
                        hits1 += (1 + (3-weights[i])*deltaSNR);
                    }
                    else
                    {
                        hits1++;
                    }
                    continuity++;
                }
                else if (results[i]==0 && desiredOutputs[i]==0)
                {
                    if (useWeight)
                    {
                        hits0 += (1 + (3-weights[i])*deltaSNR);
                    }
                    else
                    {
                        hits0++;
                    }
                    continuity++;
                }
                else
                {
                    maxContinuity = Math.max(maxContinuity, continuity);
                    continuity = 0;
                }
            }
            sumMaxContinuity += maxContinuity;
            double continuityPenalizacion = continuityImportance * (data.nSamples - (double)sumMaxContinuity) / data.nSamples;
            tree.hr0 = hits0 / n0;
            tree.hr1 = hits1 / n1;
            tree.fitness = (tree.hr0 + kHR1*tree.hr1)/(kHR1+1);
            tree.fitness -= (double)tree.nSubNodes * sizePenalization;
            tree.fitness -= continuityPenalizacion;
            return results;
        }
        
        //If we don't eval the tree:
        return null;
    }
    
}
