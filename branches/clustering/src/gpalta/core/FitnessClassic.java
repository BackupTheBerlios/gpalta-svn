/*
 * FitnessClassic.java
 *
 * Created on 13 de octubre de 2005, 08:52 PM
 *
 * Copyright (C) 2005 Neven Boric <nboric@gmail.com>
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

import gpalta.nodes.Tree;
import java.io.*;

/**
 * Implementation of the classic fitness used in GP
 * @author neven
 */
public class FitnessClassic implements Fitness
{
    
    private Output desiredOutputs;
    private double[] weights;
    private Config config;

    public void init(Config config, DataHolder data, String fileName) 
    {
        this.config = config;
        
        File classFile = new File(fileName);

        desiredOutputs = new Output(1, data.nSamples);

        try
        {
            BufferedReader out = new BufferedReader(new FileReader(classFile));

            //Find out if we are using snr info:
            boolean useWeight = false;
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
                    desiredOutputs.getArray(0)[sample] = Double.parseDouble(vals[0]);
                    //Then the weight:
                    weights[sample] = Double.parseDouble(vals[1]);
                }
                else
                {
                    desiredOutputs.getArray(0)[sample] = Double.parseDouble(line);
                }
            }
            
            Logger.log("Using classic (generic) fitness");
            Logger.log("Fitness initialized from file \"" + fileName + "\"");
            if (useWeight)
            {
                Logger.log("\t Using weight data");
            }
            Logger.log("\t Samples:              " + data.nSamples);

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

    public void init(Config config, DataHolder data, Output desiredOutputs, double[] weights) 
    {
        this.config = config;
        this.desiredOutputs = desiredOutputs;
        this.weights = weights;
    }

    public void calculate(Output outputs, Individual ind, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        double error = 0;
        for (int i=0; i<data.nSamples; i++)
        {
            error += Math.pow (outputs.getArray(0)[i] - desiredOutputs.getArray(0)[i], 2);
        }
        ind.setFitness(1/(1+Math.sqrt(error)));
    }

    public Output getProcessedOutput(Output raw, Individual ind, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        return raw;
    }
    
}
