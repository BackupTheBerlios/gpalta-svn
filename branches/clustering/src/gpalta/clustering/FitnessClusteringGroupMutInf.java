package gpalta.clustering;

import gpalta.core.*;

/**
 * Created by IntelliJ IDEA.
 * User: nvn
 * Date: 02-08-2006
 * Time: 10:57:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class FitnessClusteringGroupMutInf implements Fitness
{
    private double[][] pxc;
    private double[] pc;
    private double[] px;
    private Config config;

    public void init(Config config, DataHolder data, String fileName)
    {
        init(config, data, null, null);
    }

    public void init(Config config, DataHolder data, Output desiredOutputs, double[] weights)
    {
        this.config = config;
        pxc = new double[config.nClasses][data.nSamples];
        px = new double[data.nSamples];
        pc = new double[config.nClasses];
        for (int i=0; i<pc.length; i++)
            pc[i] = 1./config.nClasses;
    }

    public void calculate(Output outputs, Individual ind, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        calcProb(outputs, data, ind);
        double fitness = mutualInformation(px, pc, pxc);
        ind.setFitness(fitness);
        BufferedTree t;
        for (int i = 0; i < config.nClasses; i++)
        {
            t = ((TreeGroup) ind).getTree(i);
            //if (fitness > t.readFitness())
            //    t.setFitness(penalizedFitness(fitness, t.getMaxDepthFromHere()));
            t.setFitness(t.readFitness() + penalizedFitness(fitness, t.getMaxDepthFromHere())/t.nGroups);
        }
    }

    private void calcProb(Output outputs, DataHolder data, Individual ind)
    {
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            int nBasis = ((TreeGroup)ind).getTree(wClass).getSize()/2 + 1;
            for (int wSample=0; wSample<data.nSamples; wSample++)
                pxc[wClass][wSample] = Math.exp(-outputs.getArray(wClass)[wSample]/nBasis);
        }

        for (int wSample=0; wSample<data.nSamples; wSample++)
        {
            px[wSample] = 0;
            for (int wClass=0; wClass<config.nClasses; wClass++)
                px[wSample] += pc[wClass] * pxc[wClass][wSample];
        }
    }

    public double mutualInformation(double[] px, double[] py, double[][] pxy)
    {
        double mi = 0;
        for (int y=0; y<py.length; y++)
        {
            double s = 0;
            for (int x=0; x<px.length; x++)
            {
                s += pxy[y][x] * Math.log(pxy[y][x] / px[x]);
            }
            mi += py[y] * s;
        }
        return mi/Math.log(2);
    }

    protected double penalizedFitness(double fitness, int depth)
    {
        return (1 - config.sizePenalization * depth / config.maxDepth) * fitness;
    }

    public Output getProcessedOutput(Output raw, Individual ind, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        calcProb(raw, data, ind);
        for (int wClass=0; wClass<config.nClasses; wClass++)
            for (int wSample=0; wSample<data.nSamples; wSample++)
                pxc[wClass][wSample] = pxc[wClass][wSample]*pc[wClass]/px[wSample];
        ClusteringOutput processed = new ClusteringOutput(config.nClasses, data.nSamples);
        for (int i = 0; i < config.nClasses; i++)
        {
            processed.setArray(i, Common.copy(pxc[i]));
        }
        processed.setPertenenceCopy(pxc);
        return processed;
    }
}
