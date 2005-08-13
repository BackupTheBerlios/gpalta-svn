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
 * It's no longer abstract, so an instance must be created and passed around
 * This was done to fix a problen when passing a large matrix from Matlab, the
 * data somehow dissapeared if this class was abstract
 * @author neven
 */
public class RealDataHolder
{
    
    /* Every row in data correponds to all the samples for a variable. 
     * This is done to be able to return all the samples for a certain variable 
     * as a vector.
     */
    private double[][] data;
    public int nSamples;
    private int currentSample;
    public int nVars;
    
    public double getData(int whichVar)
    {
        return data[whichVar-1][currentSample];
    }
    
    public double[] getDataVect(int whichVar)
    {
        return data[whichVar-1];
    }
    
    /**
     * Initialize the data from a file
     */
    public RealDataHolder(String fileName)
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
    public RealDataHolder(double[][] data)
    {
        this.data = data;
        nVars = data.length;
        nSamples = data[0].length;
        currentSample = 0;
    }
    
    public void reset()
    {
        currentSample = 0;
    }
    
    public void update()
    {
        currentSample++;
    }
    
}
