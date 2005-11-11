/*
 * Fitness.java
 *
 * Created on 19 de mayo de 2005, 05:46 PM
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
    void init(Config config, DataHolder data, String fileName);
    
    
    /**
     * Creates a new instance of Fitness, receiving the desired outputs and
     * the SNR for each sample. Assumes the size of both parameters is
     * the same as evo.realDataHolder.nSamples
     */
    void init(Config config, DataHolder data, double[] desiredOutputs, double[] weights);
    
    /**
     * Evaluates the tree in every sample, and then calculates its hr1 and hr0
     * and its fitness, recording them in the tree.
     *
     * @return The output of the Tree for every sample, or null if the Tree wasn't
     * evaluated
     */
    double[] calculate(Tree tree, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev);
    
}
