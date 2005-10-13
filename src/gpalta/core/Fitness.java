/*
 * Fitness.java
 *
 * Created on 19 de mayo de 2005, 05:46 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package gpalta.core;

import gpalta.nodes.*;

/**
 * Hold desired outputs for fitness cases, and calculates the fitness for a given 
 * Tree
 * @author DSP
 */
public interface Fitness
{
    
    /** 
     * Creates a new instance of Fitness, reading desired outputs from file
     * 
     * @param fileName The file to read
     */
    void init(Evolution evo, String fileName);
    
    
    /**
     * Creates a new instance of Fitness, receiving the desired outputs and
     * the SNR for each sample. Assumes the size of both parameters is
     * the same as evo.realDataHolder.nSamples
     */
    void init(Evolution evo, double[] desiredOutputs, double[] weights);
    
    /**
     * Evaluates the tree in every sample, and then calculates its hr1 and hr0
     * and its fitness, recording them in the tree.
     *
     * @return The output of the Tree for every sample, or null if the Tree wasn't
     * evaluated
     */
    double[] calculate(Tree tree);
    
}
