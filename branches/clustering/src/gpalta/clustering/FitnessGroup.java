package gpalta.clustering;

import gpalta.core.Fitness;
import gpalta.core.Individual;
import gpalta.core.Config;

/**
 * Created by IntelliJ IDEA. User: nvn Date: 29-08-2006 Time: 05:54:06 PM To change this template
 * use File | Settings | File Templates.
 */
public abstract class FitnessGroup implements Fitness
{
    protected void assignFitness(Individual ind, double groupFitness, double[] treeFitness, Config config)
    {
        ind.setFitness(groupFitness);
        BufferedTree t;
        for (int i = 0; i < config.nClasses; i++)
        {
            t = ((TreeGroup) ind).getTree(i);

            //average
            t.setFitness(t.readFitness() + penalizedFitness(treeFitness[i], t.getMaxDepthFromHere(), config)/t.nGroups);

            /*
            //max
            if (fitness > t.readFitness())
                t.setFitness(penalizedFitness(treeFitness[i]));
            */
        }
    }

    protected double penalizedFitness(double fitness, int depth, Config config)
    {
        return (1 - config.sizePenalization * depth / config.maxDepth) * fitness;
    }
}
