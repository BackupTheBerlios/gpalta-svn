/*
 * FitnessClassic.java
 *
 * Created on 13 de octubre de 2005, 08:52 PM
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

import gpalta.nodes.Tree;

/**
 *
 * @author neven
 */
public class FitnessClassic implements Fitness
{
    
    private double[] desiredOutputs;
    private double[] weights;
    private double[] results;
    private Evolution evo;

    public void init(Evolution evo, String fileName) 
    {
        //TODO: implement this
    }

    public void init(Evolution evo, double[] desiredOutputs, double[] weights) 
    {
        this.evo = evo;
        this.desiredOutputs = desiredOutputs;
        this.weights = weights;
        results = new double[evo.dataHolder.nSamples];
    }

    public double[] calculate(Tree tree)
    {
        double error = 0;
        evo.dataHolder.reset();
        evo.previousOutputHolder.reset();
        if (evo.config.nPreviousOutput == 0 && evo.config.useVect)
        {
            tree.evalVect(evo, results);
        }
        else
        {
            for (int i=0; i<evo.dataHolder.nSamples; i++)
            {
                results[i] = tree.eval(evo);
                evo.dataHolder.update();
                evo.previousOutputHolder.update(results[i]);
            }
        }
        for (int i=0; i<evo.dataHolder.nSamples; i++)
        {
            error += Math.pow (results[i] - desiredOutputs[i], 2);
        }
        tree.fitness = 1/(1+Math.sqrt(error));
        return results;
    }
    
}
