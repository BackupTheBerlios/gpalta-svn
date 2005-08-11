/*
 * RealDataHolder.java
 *
 * Created on 12 de mayo de 2005, 02:43 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package GPalta;

import java.io.*;
import java.util.*;

/**
 * Holds the problem's data and provides methods to access it
 * It's abstract in order to be always available, without having to pass an
 * instance around. Instead of a constructor, use the init method
 * @author neven
 */
public abstract class RealDataHolder
{
    
    /* Every row in data correponds to all the samples for a variable. 
     * This is done to be able to return all the samples for a certain variable 
     * as a vector.
     */
    private static double[][] data;
    public static int nSamples;
    private static int currentSample;
    public static int nVars;
    
    public static double getData(int whichVar)
    {
        return data[whichVar-1][currentSample];
    }
    
    public static double[] getDataVect(int whichVar)
    {
        return data[whichVar-1];
    }
    
    /**
     * Initialize the data from a file
     */
    public static void init(String fileName)
    {
        File dataFile = new File(fileName);
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(dataFile));
            
            //count vars:
            nVars = in.readLine().trim().split("\\s+").length;

            //count samples:
            for (nSamples=1; in.readLine()!=null; nSamples++);
            
            data = new double[nVars][nSamples];
            
            in = new BufferedReader(new FileReader(dataFile));
            for (int sample=0; sample<nSamples; sample++)
            {
                String[] vars = in.readLine().trim().split("\\s+");
                for (int var=0; var<nVars; var++)
                {
                    data[var][sample] = Double.parseDouble(vars[var]);
                }
            }
            
            currentSample = 0;
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
    
    /**
     * Initialize the data from the given matrix. Every row is a variable and
     * every column is a sample
     */
    public static void init(double[][] data)
    {
        RealDataHolder.data = data;
        nVars = data.length;
        nSamples = data[0].length;
        currentSample = 0;
    }
    
    public static void reset()
    {
        currentSample = 0;
    }
    
    public static void update()
    {
        currentSample++;
    }
    
}
