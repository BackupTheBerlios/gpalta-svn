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
 *
 * @author neven
 */
public abstract class Common
{

    /**
     * A random number generator for use by other classes
     */
    public static java.util.Random globalRandom = new java.util.Random();

    private static int lastN;

    /**
     * Efficient permutation algorithm, O(n)
     *
     * @param n The size of the permutation
     * @return A permutation of size n. That is, an int array consisting of all
     *         integer numbers between 0 and n (non-inclusive) in random order
     */
    public static int[] randPerm(int n)
    {
        int[] permutation = new int[n];
        for (int i = 0; i < n; i++)
        {
            permutation[i] = i;
        }
        lastN = n;

        /* At the end of each iteration:
        * Between 0 and i is the sequence of indexes already selected
        * Between i+1 and n-1 are the indexes not selected so far (in any order)
        * The loop goes until i<n-1 because when i=n-1 it does nothing
        * (always swaps with itself)
        */
        for (int i = 0; i < n - 1; i++)
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
     *
     * @param x the array
     */
    public static void sigmoid(double[] x)
    {
        for (int i = 0; i < x.length; i++)
        {
            x[i] = 1 / (1 + Math.exp(-x[i]));
        }
    }

    public static double[][] readFromFile(String fileName, String separator) throws IOException
    {
        File dataFile = new File(fileName);

        BufferedReader in = new BufferedReader(new FileReader(dataFile));

        //count columns:
        int nCols = in.readLine().trim().split("\\s+").length;

        //count rows:
        int nRows;
        for (nRows = 1; in.readLine() != null; nRows++) ;

        double[][] data = new double[nRows][nCols];

        in = new BufferedReader(new FileReader(dataFile));
        for (int row = 0; row < nRows; row++)
        {
            String[] vars = in.readLine().trim().split(separator);
            for (int col = 0; col < nCols; col++)
            {
                data[row][col] = Double.parseDouble(vars[col]);
            }
        }

        return data;
    }

    public static double[][] transpose(double[][] m)
    {
        int nRows = m.length;
        int nCols = m[0].length;
        double[][] mT = new double[nCols][nRows];

        for (int row = 0; row < nRows; row++)
            for (int col = 0; col < nCols; col++)
                mT[col][row] = m[row][col];

        return mT;
    }

    public static void maxPerRowInline(double[][] m)
    {
        for (int i=0; i<m.length; i++)
        {
            int maxi = maxI(m[i]);
            for (int j=0; j<m[i].length; j++)
            {
                m[i][j] = 0;
            }
            m[i][maxi] = 1;
        }
    }

    public static void maxPerColInline(double[][] m)
    {
        for (int wCol =0; wCol <m[0].length; wCol++)
        {
            int winner = 0;
            double maxx = 0;
            for (int wRow =0; wRow <m.length; wRow++)
            {
                if (m[wRow][wCol] > maxx)
                {
                    winner = wRow;
                    maxx = m[wRow][wCol];
                }
            }
            for (int wRow =0; wRow <m.length; wRow++)
            {
                m[wRow][wCol] = 0;
            }
            m[winner][wCol] = 1;
        }
    }

    public static int maxI(double[] x)
    {
        int maxi = 0;
        double maxx = 0;
        for (int i=0; i<x.length; i++)
        {
            if (x[i] > maxx)
            {
                maxx = x[i];
                maxi = i;
            }
        }
        return maxi;
    }

    public static double sum(double[] x)
    {
        double sum=0;
        for (int i=0; i<x.length; i++)
        {
            sum += x[i];
        }
        return sum;
    }

    public static double[][] copy(double[][] m)
    {
        double[][] out = new double[m.length][];
        for (int i=0; i<m.length; i++)
        {
            out[i] = copy(m[i]);
        }
        return out;
    }

    public static double[] copy(double[] x)
    {
        double[] out = new double[x.length];
        System.arraycopy(x, 0, out, 0, x.length);
        return out;
    }

    public static double dist(double[] x1, double[] x2, int n)
    {
        double d = 0;
        for (int i=0; i<x1.length; i++)
            d += Math.pow(Math.abs(x1[i] - x2[i]), n);
        return Math.pow(d, (double)1/n);
    }

    public static double dist2(double[] x1, double[] x2)
    {
        double d = 0;
        double p;
        for (int i=0; i<x1.length; i++)
        {
            p = x1[i] - x2[i];
            d += p*p;
        }
        return Math.sqrt(d);
    }

}
