/*
 * Fitness.java
 *
 * Created on 19 de mayo de 2005, 05:46 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package GPalta;

import nodes.*;
import ops.*;
import java.util.*;
import java.io.*;
/**
 *
 * @author DSP
 */
public class Fitness
{
    private boolean[] classes;
    private double[] snrs;
    private int n0;
    private int n1;
    private double sizePenalization;
    private boolean useSNR;
    private double deltaSNR;
    private double kHR1;
    
    /** Creates a new instance of Fitness */
    public Fitness()
    {
        //TODO: The file should be customizable via constructor or Config class
        File classFile = new File("class.txt");
        
        /* How much each SNR is more important than the next one:
         * Must be smaller than 1/3
         */
        deltaSNR = 0.05;
        //How much important is voice over silence:
        kHR1 = 4;
        
        sizePenalization = 1/ (500*Math.pow(2,Config.maxDepth+1));
        
        classes = new boolean[RealDataHolder.nSamples];
        n0 = 0;
        n1 = 0;
        
        try
        {
            BufferedReader out = new BufferedReader(new FileReader(classFile));

            //Find out if we are using snr info:
            useSNR = false;
            String line = out.readLine().trim();
            if (line.split("\\s+").length == 2)
            {
                useSNR = true;
                snrs = new double[RealDataHolder.nSamples];
            }
            out = new BufferedReader(new FileReader(classFile));
            
            
            for (int sample=0; sample<RealDataHolder.nSamples; sample++)
            {
                line = out.readLine().trim();
                if (useSNR)
                {
                    String[] vals = line.split("\\s+");
                    //First comes the class:
                    double num = Double.parseDouble(vals[0]);
                    classes[sample] = (num==1);
                    //Then the snr:
                    double snr = Double.parseDouble(vals[1]);
                    snrs[sample] = snr;
                }
                else
                {
                    double num = Double.parseDouble(line);
                    classes[sample] = (num==1);
                }
                if (!classes[sample])
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
            if (useSNR)
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
            System.exit(-1);
        }
        catch (IOException e)
        {
            Logger.log(e);
            System.exit(-1);
        }
        catch (NumberFormatException e)
        {
            Logger.log(e);
            System.exit(-1);
        }
    }
    
    void calculate(Tree tree)
    {
        if (!tree.fitCalculated)
        {
            RealDataHolder.reset();
            LogicDataHolder.reset();
            double hits0 = 0;
            double hits1 = 0;
            int maxContinuity = 0;
            int sumMaxContinuity = 0;
            boolean previousTarget = false;
            int continuity = 0;
            boolean[] results;
            if (Config.nPreviousOutput == 0 && Config.useVect)
            {
                results = tree.evalVect();
            }
            else
            {
                results = new boolean[RealDataHolder.nSamples];
                for (int i=0; i<RealDataHolder.nSamples; i++)
                {
                    results[i] = tree.eval();
                    RealDataHolder.update();
                    LogicDataHolder.update(results[i]);
                }
            }
            for (int i=0; i<RealDataHolder.nSamples; i++)
            {
                if (classes[i]!=previousTarget)
                {
                    previousTarget = classes[i];
                    sumMaxContinuity += maxContinuity;
                    maxContinuity= 0;
                }
                if (results[i] && classes[i])
                {
                    if (useSNR)
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
                        hits1 += (1 + (3-snrs[i])*deltaSNR);
                    }
                    else
                    {
                        hits1++;
                    }
                    continuity++;
                }
                else if (!results[i] && !classes[i])
                {
                    if (useSNR)
                    {
                        hits0 += (1 + (3-snrs[i])*deltaSNR);
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
            double continuityPenalizacion = .001 * (RealDataHolder.nSamples - (double)sumMaxContinuity) / RealDataHolder.nSamples;
            tree.hr0 = hits0 / n0;
            tree.hr1 = hits1 / n1;
            tree.fitness = (tree.hr0 + kHR1*tree.hr1)/(kHR1+1);
            tree.fitness -= (double)tree.nSubNodes * sizePenalization;
            tree.fitness -= continuityPenalizacion;
        }
    }
    
}
