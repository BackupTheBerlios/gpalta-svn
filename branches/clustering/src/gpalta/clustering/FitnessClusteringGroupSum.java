package gpalta.clustering;

/**
 * Created by IntelliJ IDEA.
 * User: neven
 * Date: 11-07-2006
 * Time: 05:58:06 PM
 * To change this template use File | Settings | File Templates.
 */
/*public class FitnessClusteringGroupSum extends FitnessClusteringGroup
{
    public double[][] d;
    public double paretoRadius;
    public IndexedDistance[][] id;
    public void calculate(Output outputs, Individual ind, TempOutputFactory evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        for (int wSample = 0; wSample < data.nSamples; wSample++)
            for (int wClass = 0; wClass < config.nClasses; wClass++)
                prob[wClass][wSample] = outputs.getArray(wClass)[wSample];
        Common.maxPerColInline(prob);

        double error = 0;
        double nTot = 0;
        for (int wClass = 0; wClass < config.nClasses; wClass++)
        {
            //double nThisClass = Common.sum(pxy[wClass]);
            double nThisClass = 0;
            double errorThisClass = 0;
            int nSumThisClass = 0;
            for (int s1=0; s1 < data.nSamples; s1++)
            {
                if (prob[wClass][s1] == 1)
                {
                    int n=0;
                    int n2=0;
                    double e = 0;
                    for (int s2=1; id[s1][s2]!=null; s2++)
                    {
                        n2++;
                        if (prob[wClass][id[s1][s2].index] == 1)
                        {
                            e += id[s1][s2].d;
                            n++;
                            nSumThisClass++;
                        }
                    }
                    nTot += n/n2;
                    errorThisClass += e;
                }
            }
            error += errorThisClass/nSumThisClass;
            if (Common.sum(prob[wClass])==0)
                nTot = Integer.MIN_VALUE;
        }

        double fitness = 1 / (1 + error);
        fitness = nTot/data.nSamples;

        ind.setFitness(fitness);
        Tree t;
        for (int i = 0; i < config.nClasses; i++)
        {
            t = ((TreeGroup) ind).getTree(i);
            if (fitness > t.readFitness())
                t.setFitness(penalizedFitness(fitness, t.getMaxDepthFromHere()));
        }
    }

    public Output getProcessedOutput(Output raw, Individual ind, TempOutputFactory evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        return raw;
    }

    public void init(Config config, DataHolder data, Output desiredOutputs, double[] weights)
    {
        super.init(config, data, desiredOutputs, weights);

        d = new double[data.nSamples][data.nSamples];

        double[] x1 = new double[data.nVars];
        double[] x2 = new double[data.nVars];
        for (int s1=0; s1 < data.nSamples-1; s1++)
        {
            for (int wVar = 0; wVar < data.nVars; wVar++)
                x1[wVar] = data.getDataVect(wVar+1)[s1];
            for (int s2=s1+1; s2<data.nSamples; s2++)
            {
                for (int wVar = 0; wVar < data.nVars; wVar++)
                    x2[wVar] = data.getDataVect(wVar+1)[s2];
                d[s1][s2] = d[s2][s1] = Common.dist2(x1, x2);
            }
        }
        double[] r = new double[data.nSamples];
        int n = (int)(0.2 * data.nSamples);

        id = new IndexedDistance[data.nSamples][data.nSamples];
        for (int i =0; i < data.nSamples; i++)
        {
            for (int j=0; j<data.nSamples; j++)
            {
                id[i][j] = new IndexedDistance(j, d[i][j]);
            }
            Arrays.sort(id[i], new IndexedDistanceComparator());
            r[i] = id[i][n].d;
        }
        paretoRadius = Common.sum(r)/data.nSamples;
        for (int i =0; i < data.nSamples; i++)
        {
            for (int j=0; j<data.nSamples; j++)
            {
                if (id[i][j].d > paretoRadius)
                {
                    id[i][j] = null;
                    break;
                }
            }
        }
    }
}

class IndexedDistance
{
    public int index;
    public double d;
    public IndexedDistance(int index, double d)
    {
        this.index = index;
        this.d = d;
    }
}

class IndexedDistanceComparator implements Comparator<IndexedDistance>
{

    public int compare(IndexedDistance o1, IndexedDistance o2)
    {
        if (o1.d < o2.d)
        {
            return (-1);
        }
        else if (o1.d > o2.d)
        {
            return (1);
        }
        else
        {
            return (0);
        }

    }

}
*/