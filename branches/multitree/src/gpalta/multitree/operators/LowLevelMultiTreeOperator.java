package gpalta.multitree.operators;

import gpalta.ops.TreeOperator;
import gpalta.core.*;
import gpalta.multitree.MultiTreeIndividual;
import gpalta.multitree.MultiOutput;
import gpalta.clustering.InformationTheory;

/**
 * Created by IntelliJ IDEA. User: nvn Date: 21-03-2007 Time: 03:45:37 PM To change this template
 * use File | Settings | File Templates.
 */
public abstract class LowLevelMultiTreeOperator
{
    protected TreeOperator treeOp;
    public int timesApplied;
    public int timesFitnessImproved;

    public LowLevelMultiTreeOperator(TreeOperator treeOp)
    {
        this.treeOp = treeOp;
    }

    public abstract boolean operate(MultiTreeIndividual[] individuals, TempVectorFactory tempVectorFactory, ProblemData problemData);
    public abstract int nIndividuals();

    public int selectWorstTree(MultiTreeIndividual ind)
    {
        int min = 0;
        double minFit = ind.getTree(0).readFitness();
        for (int i=1; i< ind.nTrees(); i++)
        {
            if (ind.getTree(i).readFitness() < minFit)
            {
                minFit = ind.getTree(i).readFitness();
                min = i;
            }
        }
        return min;
    }

    public int selectBestTree(MultiTreeIndividual ind)
    {
        int max = 0;
        double maxFit = ind.getTree(0).readFitness();
        for (int i=1; i< ind.nTrees(); i++)
        {
            if (ind.getTree(i).readFitness() > maxFit)
            {
                maxFit = ind.getTree(i).readFitness();
                max = i;
            }
        }
        return max;
    }

    public int selectMostSimilarTree(Tree tree1, MultiTreeIndividual multiTree2, TempVectorFactory tempVectorFactory, ProblemData problemData)
    {
        SingleOutput out1 = new SingleOutput(problemData.nSamples);
        tree1.evalVect(out1, tempVectorFactory, problemData);
        Common.sigmoid(out1.x);

        MultiOutput out2 = new MultiOutput(multiTree2.nTrees(), problemData.nSamples);
        multiTree2.evalVect(out2, tempVectorFactory, problemData);
        for (int i=0; i<out2.getDim(); i++)
            Common.sigmoid(out2.getArray(i));

        int min = 0;
        double minDiff = Common.dist2(out1.x, out2.getArray(0));
        for (int i=1; i<out2.getDim(); i++)
        {
            double diff = diffBetweenOutputs(out1.x, out2.getArray(i));
            if (diff < minDiff)
            {
                min = i;
                minDiff = diff;
            }
        }
        return min;
    }

    public int selectMostSimilarTreeMutInf(Tree tree1, MultiTreeIndividual multiTree2, TempVectorFactory tempVectorFactory, ProblemData problemData)
    {
        SingleOutput out1 = new SingleOutput(problemData.nSamples);
        tree1.evalVect(out1, tempVectorFactory, problemData);
        Common.sigmoid(out1.x);

        MultiOutput out2 = new MultiOutput(multiTree2.nTrees(), problemData.nSamples);
        multiTree2.evalVect(out2, tempVectorFactory, problemData);
        for (int i=0; i<out2.getDim(); i++)
            Common.sigmoid(out2.getArray(i));

        double[] px = InformationTheory.px(out1.x, 0, 1, 100);
        int max = 0;
        double[] py = InformationTheory.px(out2.getArray(0), 0, 1, 100);
        double[][] pxy = InformationTheory.pxy(out1.x, out2.getArray(0), 0, 1, 100);
        double maxMutInf = InformationTheory.mutualInformation(px, py, pxy);
        for (int i=1; i<out2.getDim(); i++)
        {
            py = InformationTheory.px(out2.getArray(i), 0, 1, 100);
            pxy = InformationTheory.pxy(out1.x, out2.getArray(i), 0, 1, 100);
            double mutInf = InformationTheory.mutualInformation(px, py, pxy);
            if (mutInf > maxMutInf)
            {
                max = i;
                maxMutInf = mutInf;
            }
        }
        return max;
    }

    public double diffBetweenOutputs(double[] x1, double[] x2)
    {
        return Common.dist2(x1, x2);
    }

}
