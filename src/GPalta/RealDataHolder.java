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
 *
 * @author neven
 */
public abstract class RealDataHolder
{
    
    /* data is a List of double arrays. Each array corresponds to all the
     * samples for a var
     */
    private static List<double[]> data;
    public static int nSamples;
    private static int currentSample;
    public static int nVars;
    
    public static double getData(int whichVar)
    {
        return data.get(whichVar-1)[currentSample];
    }
    
    public static double[] getDataVect(int whichVar)
    {
        return data.get(whichVar-1);
    }
    
    public static void init()
    {
        File dataFile = new File("data.txt");
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(dataFile));
            
            //count vars:
            nVars = in.readLine().trim().split("\\s+").length;

            //count samples:
            for (nSamples=1; in.readLine()!=null; nSamples++);
            
            data = new ArrayList<double[]>();
            for (int var=0; var<nVars; var++)
            {
                data.add(new double[nSamples]);
            }
            
            in = new BufferedReader(new FileReader(dataFile));
            for (int sample=0; sample<nSamples; sample++)
            {
                String[] vars = in.readLine().trim().split("\\s+");
                for (int var=0; var<nVars; var++)
                {
                    data.get(var)[sample] = Double.parseDouble(vars[var]);
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
    
    public static void reset()
    {
        currentSample = 0;
    }
    
    public static void update()
    {
        currentSample++;
    }
    
}
