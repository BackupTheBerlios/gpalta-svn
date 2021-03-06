package gpalta.clustering;

/**
 * Created by IntelliJ IDEA.
 * User: nvn
 * Date: 04-08-2006
 * Time: 10:03:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class FitnessClusteringGroupGaussMixMinMax //extends FitnessGroup
{
    /*
    private double[][] d;
    private int[] min;
    private int[] nBasis;
    private Config config;
    private double[][] proto;
    private int nPerClass[];

    public void init(Config config, DataHolder data, String fileName)
    {
        init(config, data, null, null);
    }

    public void init(Config config, DataHolder data, Output desiredOutputs, double[] weights)
    {
        this.config = config;
        d = new double[config.nClasses][data.nSamples];
        min = new int[data.nSamples];
        nBasis = new int[config.nClasses];
        proto = new double[config.nClasses][data.nVars];
        nPerClass = new int[config.nClasses];
    }

    public void calculate(Output outputs, Individual ind, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        calcDist(outputs, data, ind);
        nPerClass = new int[config.nClasses];
        double[] classError = new double[config.nClasses];
        for (int wSample=0; wSample<data.nSamples; wSample++)
            nPerClass[min[wSample]]++;
        int totBasis = 0;
        for (int wClass=0; wClass<config.nClasses; wClass++)
            totBasis += nBasis[wClass];
        for (int wSample=0; wSample<data.nSamples; wSample++)
            classError[min[wSample]] += d[min[wSample]][wSample];
        double error = Common.sum(classError);
        for (int wClass=0; wClass<config.nClasses; wClass++)
            classError[wClass] = 1 / (1 + classError[wClass]/nPerClass[wClass]);

        assignFitness(ind, 1 / (1+error), classError, config);
    }

    private void calcDist(Output outputs, DataHolder data, Individual ind)
    {
        assert config.outputDimension == 2;
        for (int wClass=0; wClass<config.nClasses; wClass++)
            nBasis[wClass] = ((TreeGroup)ind).getTree(wClass).getSize()/2 + 1;
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            for (int wSample=0; wSample<data.nSamples; wSample++)
                d[wClass][wSample] = outputs.getArray(wClass*2)[wSample] + outputs.getArray(wClass*2+1)[wSample];
        }
        for (int wSample=0; wSample<data.nSamples; wSample++)
        {
            min[wSample] = 0;
            double minDist = d[0][wSample];
            for (int wClass=1; wClass<config.nClasses; wClass++)
            {
                if (d[wClass][wSample] < minDist)
                {
                    minDist = d[wClass][wSample];
                    min[wSample] = wClass;
                }
            }
        }
    }

    private void calcProto(DataHolder data)
    {
        for (int wClass=0; wClass<config.nClasses; wClass++)
            for (int wVar=0; wVar<data.nVars; wVar++)
                proto[wClass][wVar] = 0;
        double[] x;
        for (int wSample=0; wSample<data.nSamples; wSample++)
        {
            x = data.getCurrentSample(wSample);
            for (int wVar=0; wVar<data.nVars; wVar++)
                proto[min[wSample]][wVar] += x[wVar];
        }
        for (int wClass=0; wClass<config.nClasses; wClass++)
            for (int wVar=0; wVar<data.nVars; wVar++)
                proto[wClass][wVar] /= nPerClass[wClass];
    }

    protected double penalizedFitness(double fitness, int depth)
    {
        return (1 - config.sizePenalization * depth / config.maxDepth) * fitness;
    }

    public Output getProcessedOutput(Output raw, Individual ind, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        calcDist(raw, data, ind);
        ClusteringOutput processed = new ClusteringOutput(config.nClasses, data.nSamples);
        for (int i = 0; i < config.nClasses; i++)
        {
            processed.setArray(i, Common.copy(d[i]));
        }
        processed.setPertenenceCopy(d);
        return processed;
    }
    */
}


