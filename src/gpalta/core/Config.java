/*
 * Config.java
 *
 * Created on 10 de mayo de 2005, 11:46 PM
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

import java.io.*;
import java.util.*;

/**
 * Holds the GP parameters.
 */
public class Config 
{
    
    public int populationSize = 500;
    public int nGenerations = 1000;
    
    public int tournamentSize = 2;
    
    public int maxDepth = 9;
    public int initialMinDepth = 3;

    /* Upper limits for the probability regions of the tree operations. This means:
     * probability of crossover = upLimitProbCrossOver - 0
     * probability of mutation = upLimitProbMutation - upLimitProbCrossOver
     * probability of reproduction = 1 - upLimitProbMutation
     */
    public double upLimitProbCrossOver = 0.85;
    public double upLimitProbMutation = 0.9;
    //The rest is for reproduction
    
    public double constLowLimit = -100;
    public double constUpLimit = 100;
    
    public int maxCrossoverTries = 10;
    
    public double upLimitProbSelectTerminal = .1;
    public double upLimitProbSelectNonTerminal = 1;
    public double upLimitProbSelectRoot = 0;
    //The rest is for select any node
    
    public double probGrowBuild = .5;
    //The rest is for Full Build
    
    public int nPreviousOutput = 0;

    public String saveFileName = "evo.bin";
    
    public static String logFileName = "log.txt";
    
    public boolean useVect = false;
    
    //For fitness:
    
    /* How much each SNR is more important than the next one:
     * Must be smaller than 1/3
     */
    public double deltaSNR = 0.05;
    
    
    public double continuityImportance = 0.001;
    
    //How much important is voice over silence:
    public double kHR1 = 4;
    
    public boolean usePreviousOutputAsReal = false;
    
    public boolean nonInteractive = false;
    
    public int nDaysToRun = 1;
    
    /**
     * Reads config from a property file. The file must contain a value for all
     * the fields in the Config class
     * 
     * @param fileName The name of the config file
     */
    public void init(String fileName)
    {
        try
        {
            FileInputStream in = new FileInputStream(fileName);
            Properties applicationProps = new Properties();
            applicationProps.load(in);
            in.close();
            
            java.lang.reflect.Field[] fields = Config.class.getFields();
            for (int i = 0; i < fields.length; i++)
            {
                Class type = fields[i].getType();
                if (applicationProps.getProperty(fields[i].getName()) == null)
                {
                    Logger.log("Property " + fields[i].getName() + " not found in " + fileName);
                    //Do not exit, just warn and continue:
                    continue;
                }
                
                if (type.getName().equals("double"))
                {
                    fields[i].setDouble(this, Double.parseDouble(applicationProps.getProperty(fields[i].getName())));
                }
                else if (type.getName().equals("int"))
                {
                    fields[i].setInt(this, Integer.parseInt(applicationProps.getProperty(fields[i].getName())));
                }
                else if (type.getName().equals("boolean"))
                {
                    fields[i].setBoolean(this, Boolean.parseBoolean(applicationProps.getProperty(fields[i].getName())));
                }
                else if (type.getName().equals("java.lang.String"))
                {
                    fields[i].set(this, applicationProps.getProperty(fields[i].getName()));
                }
            }
            
        }
        catch (IOException e)
        {
            Logger.log(e);
        }
        catch (IllegalAccessException e)
        {
            Logger.log(e);
        }
        catch (IllegalArgumentException e)
        {
            Logger.log(e);
        }
        catch (ExceptionInInitializerError e)
        {
            Logger.log(e.getMessage());
        }
        
    }
}
