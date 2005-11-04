/*
 * LogicDataHolder.java
 *
 * Created on 30 de mayo de 2005, 06:58 PM
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

/**
 *
 * @author DSP
 */
public class PreviousOutputHolder 
{
    public int nDelays;
    private double[] prevOut;
    private int index;
    private Config config;
    
    public PreviousOutputHolder(Config config)
    {
        this.config = config;
        nDelays = config.nPreviousOutput;
        prevOut = new double[nDelays];
        for (int i=0; i<nDelays; i++)
        {
            prevOut[i] = 0;
        }
        index = 0;

        if (nDelays > 0)
            Logger.log("Using " + config.nPreviousOutput + " previous outputs as " + (config.usePreviousOutputAsReal ? "real" : "logic") + " terminals");
        
    }
    
    public void reset()
    {
        index = 0;
        for (int i=0; i<nDelays; i++)
        {
            prevOut[i] = 0;
        }
    }
    
    public void update(double currentOut)
    {
        if (nDelays != 0)
        {
            index++;
            if (index == nDelays)
            {
                index = 0;
            }
            prevOut[index] = currentOut;
        }
    }
    
    public double getData(int delay)
    {
        int tmpIndex = index - (delay - 1);
        if (tmpIndex < 0)
        {
            tmpIndex = nDelays + tmpIndex;
        }
        return prevOut[tmpIndex];
    }
}
