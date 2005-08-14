/*
 * Common.java
 *
 * Created on 11 de mayo de 2005, 12:23 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
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
    


}
