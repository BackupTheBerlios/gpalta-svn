/*
 * FitnessClassic.java
 *
 * Created on 13 de octubre de 2005, 08:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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

    public void init(Evolution evo, String fileName) {
    }

    public void init(Evolution evo, double[] desiredOutputs, double[] weights) 
    {
        this.evo = evo;
        this.desiredOutputs = desiredOutputs;
        this.weights = weights;
        results = new double[evo.realDataHolder.nSamples];
    }

    public double[] calculate(Tree tree)
    {
        double error = 0;
        evo.realDataHolder.reset();
        evo.logicDataHolder.reset();
        if (Config.nPreviousOutput == 0 && Config.useVect)
        {
            tree.evalVect(evo, results);
        }
        else
        {
            for (int i=0; i<evo.realDataHolder.nSamples; i++)
            {
                results[i] = tree.eval(evo);
                evo.realDataHolder.update();
                evo.logicDataHolder.update(results[i]);
            }
        }
        for (int i=0; i<evo.realDataHolder.nSamples; i++)
        {
            error += Math.pow (results[i] - desiredOutputs[i], 2);
        }
        tree.fitness = 1/(1+Math.sqrt(error));
        return results;
    }
    
}
