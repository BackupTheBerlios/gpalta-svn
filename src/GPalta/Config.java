/*
 * Config.java
 *
 * Created on 10 de mayo de 2005, 11:46 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package GPalta;

import java.io.*;
import java.util.*;

/**
 * Holds the GP parameters
 * It isn't abstract, to be able to create an instance and modify the static 
 * fields automatically
 * 
 */
public class Config 
{
    
    public static int populationSize = 500;
    public static int nGenerations = 1000;
    
    public static int tournamentSize = 2;
    
    public static int maxDepth = 9;
    public static int initialMinDepth = 3;

    /* Upper limits for the probability regions of the tree operations. This means:
     * probability of crossover = upLimitProbCrossOver - 0
     * probability of mutation = upLimitProbMutation - upLimitProbCrossOver
     * probability of reproduction = 1 - upLimitProbMutation
     */
    public static double upLimitProbCrossOver = 0.85;
    public static double upLimitProbMutation = 0.9;
    //The rest is for reproduction
    
    public static double constLowLimit = -100;
    public static double constUpLimit = 100;
    
    public static int maxCrossoverTries = 10;
    
    public static double upLimitProbSelectTerminal = .1;
    public static double upLimitProbSelectNonTerminal = 1;
    public static double upLimitProbSelectRoot = 0;
    //The rest is for select any node
    
    public static double probGrowBuild = .5;
    //The rest is for Full Build
    
    public static int nPreviousOutput = 0;

    public static String saveFileName = "evo.bin";
    
    public static String logFileName = "log.txt";
    
    public static boolean useVect = false;
    
    //For fitness:
    
    /* How much each SNR is more important than the next one:
     * Must be smaller than 1/3
     */
    public static double deltaSNR = 0.05;
    
    //How much important is voice over silence:
    public static double kHR1 = 4;
    
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
                    System.exit(-1);
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
