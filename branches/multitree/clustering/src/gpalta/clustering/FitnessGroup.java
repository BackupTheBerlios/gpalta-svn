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
        TreeGroup ind2 = (TreeGroup) ind;
        ind2.setFitness(groupFitness);
        BufferedTree t;
        for (int i = 0; i < ind2.nTrees(); i++)
        {
            t = ind2.getTree(i);

            //average
            t.setFitness(t.readFitness() + penalizedFitness(treeFitness[i], t.getMaxDepthFromHere(), config)/t.nGroups);


            //max
            /*
            if (treeFitness[i] > t.readFitness())
                t.setFitness(penalizedFitness(treeFitness[i], t.getMaxDepthFromHere(), config));
            */

        }
    }

    protected double penalizedFitness(double fitness, int depth, Config config)
    {
        return (1 - config.sizePenalization * depth / config.maxDepth) * fitness;
    }
}
