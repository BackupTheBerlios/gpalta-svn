package gpalta.multitree.operators;

import gpalta.ops.TreeOperator;
import gpalta.core.TempVectorFactory;
import gpalta.core.ProblemData;
import gpalta.core.Common;
import gpalta.multitree.MultiTreeIndividual;

/**
 * Created by IntelliJ IDEA. User: nvn Date: 21-03-2007 Time: 04:21:00 PM To change this template
 * use File | Settings | File Templates.
 */
public class CrossRandomTrees extends LowLevelMultiTreeOperator
{
    public CrossRandomTrees(TreeOperator op)
    {
        super(op);
    }

    public boolean operate(MultiTreeIndividual[] individuals, TempVectorFactory tempVectorFactory, ProblemData problemData)
    {
        int t1 = Common.globalRandom.nextInt(individuals[0].nTrees());
        int t2 = Common.globalRandom.nextInt(individuals[1].nTrees());
        return treeOp.crossOver(individuals[0].getTree(t1), individuals[1].getTree(t2));
    }

    public int nIndividuals()
    {
        return 2;
    }
}
