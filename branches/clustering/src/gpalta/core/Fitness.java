/*
 * Fitness.java
 *
 * Created on 19 de mayo de 2005, 05:46 PM
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

import gpalta.nodes.*;
import java.util.List;

/**
 * Hold desired outputs for fitness cases, and calculates the fitness for a given 
 * Tree
 * @author DSP
 */
public interface Fitness
{
    
    /** 
     * Initializes the Fitness, reading desired outputs from file
     * 
     * @param config The evolution config, might be needed inside the Fitness
     * @param data The current problem's data, might also be needed (for
     * instance to know the numer of samples used)
     * @param fileName The file to read
     */
    void init(Config config, DataHolder data, String fileName);
    
    
    /**
     * Initializes the Fitness, receiving the desired outputs and
     * the wheights (importance) for each sample.
     *
     * @param config The evolution config, might be needed inside the Fitness
     * @param data The current problem's data, might also be needed (for
     * instance to know the number of samples used)
     * @param desiredOutputs The desired outputs
     * @param weights The weight (importance) of each sample
     */
    void init(Config config, DataHolder data, List<double[]> desiredOutputs, double[] weights);
    
    /**
     * Evaluates the tree in every sample, and then calculates its fitness
     * (and maybe some other stats, like hr0 and hr1 for classifiers),
     * recording them in the tree.
     *
     * @return The output of the Tree for every sample, or null if the Tree wasn't
     * evaluated
     */
    double[] calculate(List<double[]> outputs, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev);
    
}
