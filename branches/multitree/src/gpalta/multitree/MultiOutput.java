/*
 * MultiOutput.java
 *
 * Created on 03-01-2007, 11:37:55 PM
 *
 * Copyright (C) 2007 Neven Boric <nboric@gmail.com>
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

package gpalta.multitree;

import gpalta.core.Logger;
import gpalta.core.Output;
import gpalta.core.Common;

public class MultiOutput extends Output
{
    private double[][] data;
    private int dim;

    /**
     * Creates a new instance of Output
     */
    public MultiOutput(int dim, int nSamples)
    {
        data = new double[dim][nSamples];
        this.dim = dim;
    }

    public Object clone()
    {
        MultiOutput out = null;
        try
        {
            /* Remember to clone each array and their contents */
            out = (MultiOutput) super.clone();
            out.data = new double[data.length][];
            for (int i = 0; i < dim; i++)
                out.store(i, getArrayCopy(i));
        }
        catch (CloneNotSupportedException e)
        {
            Logger.log(e);
        }
        return out;
    }

    /**
     * Get an array representing outputs for each sample
     *
     * @param wDim Which dimension of the output to get. For uni-dimensional problems, this should
     *             be zero
     * @return A pointer to the actual array. A pointer is returned instead of a copy for
     *         efficiency. Modifying the contents of this array can be problematic if this output
     *         object is associated with an individual
     */
    public double[] getArray(int wDim)
    {
        return data[wDim];
    }

    /**
     * Register an array to this Output object. Note that this will only set a pointer to the actual
     * array. This is done to avoid calling a method for every sample. Setting all the values on an
     * array and then linking to it is much faster than calling a method every time. Consequently,
     * if this output is going to be stored, caution must be taken to not modify the contents of the
     * pointed array unintentionally.
     *
     * @param wDim
     * @param array
     */
    public void store(int wDim, double[] array)
    {
        System.arraycopy(array, 0, data[wDim], 0 , data[wDim].length);
    }

    /**
     * Get an independent array representing outputs for each sample
     *
     * @param wDim Wich dimension of the output to get. For uni-dimensional problems, this should be
     *             zero
     * @return A new array with all the outputs
     */
    public double[] getArrayCopy(int wDim)
    {
        return Common.copy(data[wDim]);
    }

    /**
     * Get the dimension (number of scalar outputs per sample) of this Output
     */
    public int getDim()
    {
        return dim;
    }
}
