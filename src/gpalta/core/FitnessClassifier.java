/*
 * FitnessClassifier.java
 *
 * Created on 13 de octubre de 2005, 03:37 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package gpalta.core;

import gpalta.nodes.*;
import gpalta.ops.*;
import java.util.*;
import java.io.*;

/**
 *
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
    private Evolution evo;
    private double[] results;
    
    /** 
     * Creates a new instance of Fitness, initializing only constants
     */
    private void init (Evolution evo)
    {
        this.evo = evo;
        /* How much each SNR is more important than the next one:
         * Must be smaller than 1/3
         */
        deltaSNR = Config.deltaSNR;
        continuityImportance = Config.continuityImportance;
        //How much important is voice over silence:
        kHR1 = Config.kHR1;
        
        sizePenalization = 1/ (500*Math.pow(2,Config.maxDepth+1));
        
        results = new double[evo.realDataHolder.nSamples];
    }
    
    /** 
     * Creates a new instance of Fitness, reading desired outputs from file
     * 
     * @param fileName The file to read
     */
    public void init(Evolution evo, String fileName)
    {
        // Create a new Fitness, and only init constants:
        init(evo);

        File classFile = new File(fileName);

        desiredOutputs = new double[evo.realDataHolder.nSamples];
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
                weights = new double[evo.realDataHolder.nSamples];
            }
            out = new BufferedReader(new FileReader(classFile));


            for (int sample=0; sample<evo.realDataHolder.nSamples; sample++)
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

            Logger.log("Fitness initialized from file \"class.txt\"");
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
    
    /**
     * Creates a new instance of Fitness, receiving the desired outputs and
     * the SNR for each sample. Assumes the size of both parameters is
     * the same as evo.realDataHolder.nSamples
     */
    public void init (Evolution evo, double[] desiredOutputs, double[] weights)
    {
        // Create a new Fitness, and only init constants:
        init(evo);
        
        this.desiredOutputs = desiredOutputs;
        this.weights = weights;
        for (int sample=0; sample<evo.realDataHolder.nSamples; sample++)
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
    
    /**
     * Evaluates the tree in every sample, and then calculates its hr1 and hr0
     * and its fitness, recording them in the tree.
     *
     * @return The output of the Tree for every sample, or null if the Tree wasn't
     * evaluated
     */
    public double[] calculate(Tree tree)
    {
        
        if (!tree.fitCalculated)
        {
            evo.realDataHolder.reset();
            evo.logicDataHolder.reset();
            double hits0 = 0;
            double hits1 = 0;
            int maxContinuity = 0;
            int sumMaxContinuity = 0;
            double previousTarget = 0;
            int continuity = 0;
            if (Config.nPreviousOutput == 0 && Config.useVect)
            {
                tree.evalVect(evo, results);
            }
            else
            {
                for (int i=0; i<evo.realDataHolder.nSamples; i++)
                {
                    results[i] = tree.eval(evo);
                    evo.realDataHolder.update();
                    evo.logicDataHolder.update(results[i]);
                }
            }
            for (int i=0; i<evo.realDataHolder.nSamples; i++)
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
            double continuityPenalizacion = continuityImportance * (evo.realDataHolder.nSamples - (double)sumMaxContinuity) / evo.realDataHolder.nSamples;
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
