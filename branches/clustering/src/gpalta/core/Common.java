/*
 * Common.java
 *
 * Created on 11 de mayo de 2005, 12:23 AM
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
 * Useful utilities for all clases
 * @author neven
 */
public abstract class Common
{
    
    /**
     * A random number generator for use by other classes
     */
    public static java.util.Random globalRandom = new java.util.Random();
    
    private static int lastN;
    private static int[] permutation;
    
    /** 
     * Efficient permutation algorithm, O(n)
     *
     * @param n The size of the permutation
     *
     * @return A permutation of size n. That is, an int array consisting of all
     * integer numbers between 0 and n (non-inclusive) in random order
     */
    public static int[] randPerm(int n)
    {
        /* If we need a permutation of the same size as the last time, we can
         * use the same array whithout reinitializing it.
         */ 
        if (n != lastN)
        {
            permutation = new int[n];        
            for (int i=0; i<n; i++)
            {
                permutation[i] = i;
            }
        }
        lastN = n;
        
        /* At the end of each iteration:
         * Between 0 and i is the sequence of indexes already selected
         * Between i+1 and n-1 are the indexes not selected so far (in any order)
         * The loop goes until i<n-1 because when i=n-1 it does nothing
         * (always swaps with itself)
         */
        for (int i=0; i<n-1; i++)
        {
            //choose a random position between i and n-1
            int which = i + globalRandom.nextInt(n - i);
            //swap permutation[i] and permutation[which]
            int tmp = permutation[i];
            permutation[i] = permutation[which];
            permutation[which] = tmp;
        }
        
        return permutation;
    }

    /**
     * Calculate the sigmoid (logistic function) for an entire array (inplace, i.e. modifying its contents)
     * @param x the array
     */
    public static void sigmoid(double[] x)
    {
        for (int i = 0; i < x.length; i++)
        {
            x[i] = 1/(1 + Math.exp(-x[i]));
        }
    }
    
}
