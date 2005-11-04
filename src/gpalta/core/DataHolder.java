/*
 * RealDataHolder.java
 *
 * Created on 12 de mayo de 2005, 02:43 AM
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
 * Holds the problem's data and provides methods to access it.
 * It's no longer abstract, so an instance must be created and passed around
 * This was done to fix a problen when passing a large matrix from Matlab 
 * (the data sometimes 'dissapeared' if this class was abstract)
 * @author neven
 */
public class DataHolder
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
    public DataHolder(String fileName)
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
     * Initialize the data from the given matrix. Every row is a variable and
     * every column is a sample
     */
    public DataHolder(double[][] data)
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
