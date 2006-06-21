/*
 * Output.java
 *
 * Created on 31 de mayo de 2006, 11:09 PM
 *
 * Copyright (C) 2006 Neven Boric <nboric@gmail.com>
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
 * @author neven
 */
public class Output implements Cloneable
{
    private double[][] data;
    private int nArrays;

    /** Creates a new instance of Output */
    public Output(int nArrays, int nSamples)
    {
        data = new double[nArrays][nSamples];
        this.nArrays = nArrays;
    }

    public Object clone()
    {
        Output out = null;
        try
        {
            out = (Output)super.clone();
            out.data = data.clone();
        }
        catch (CloneNotSupportedException e)
        {
            Logger.log(e);
        }
        return out;
    }
    
    public double[] getArray(int which)
    {
        return data[which];
    }

    public void setArray(int which, double[] array)
    {
        data[which] = array;
    }
    
    public double[] getArrayCopy(int which)
    {
        double[] out = new double[data[which].length];
        System.arraycopy(data[which], 0, out, 0, data[which].length);
        return out;
    }
    
    /*public void setArrayCopy(int which, double[] array)
    {
        data[which] = array;
    }*/
    
    public int nArrays()
    {
        return nArrays;
    }
    
}
