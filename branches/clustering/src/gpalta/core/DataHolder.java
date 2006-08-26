/*
 * RealDataHolder.java
 *
 * Created on 12 de mayo de 2005, 02:43 AM
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

import java.io.*;

/**
 * Holds the problem's data and provides methods to access it.
 * It's no longer abstract, so an instance must be created and passed around
 * This was done to fix a problen when passing a large matrix from Matlab
 * (the data sometimes 'dissapeared' if this class was abstract)
 *
 * @author neven
 */
public class DataHolder
{

    /* Every row in data correponds to all the samples for a variable. 
    * This is done to be able to return all the samples for a certain variable
    * as a vector.
    */
    private double[][] data;
    private double[][] dataT;
    private double[][] ranges;
    public int nSamples;
    private int currentSample;
    public int nVars;

    public double getData(int whichVar)
    {
        return data[whichVar - 1][currentSample];
    }

    public double[] getDataVect(int whichVar)
    {
        return data[whichVar - 1];
    }

    /**
     * Initialize the data from a file
     */
    public DataHolder(String fileName)
    {
        try
        {
            dataT = Common.readFromFile(fileName, "\\s+");
            data = Common.transpose(dataT);
            nVars = data.length;
            nSamples = data[0].length;
            calcRange();
            Logger.log("Finished reading " + nSamples + " samples from file \"" + fileName + "\"");
        }

        /* TODO: This exception shouldn't be caught here, but thrown to the
         * evolution and then to the controller
         */
        catch (IOException e)
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
        dataT = Common.transpose(data);
        nVars = data.length;
        nSamples = data[0].length;
        currentSample = 0;
        calcRange();
    }

    private void calcRange()
    {
        ranges = new double[nVars][3];
        for (int wVar=0; wVar<nVars; wVar++)
        {
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for (int wSample=0; wSample<nSamples; wSample++)
            {
                min = Math.min(min, data[wVar][wSample]);
                max = Math.max(max, data[wVar][wSample]);
            }
            ranges[wVar][0] = min;
            ranges[wVar][1] = max;
            ranges[wVar][2] = max-min;
        }
    }

    /**
     * reset() must be called every time a new tree is being evaluated
     * (when using eval() instead of evalVect() )
     */
    public void reset()
    {
        currentSample = 0;
    }

    /**
     * update() must be called every time a new sample is required
     * (when using eval() instead of evalVect() )
     */
    public void update()
    {
        currentSample++;
    }

    public double getMin(int whichVar)
    {
        return ranges[whichVar-1][0];
    }

    public double getMax(int whichVar)
    {
        return ranges[whichVar-1][1];
    }

    public double getRange(int wVar)
    {
        return ranges[wVar-1][2];
    }

    public double[] getAllVars()
    {
        return dataT[currentSample];
    }

    public double[] getAllVars(int wSample)
    {
        return dataT[wSample];
    }



}
