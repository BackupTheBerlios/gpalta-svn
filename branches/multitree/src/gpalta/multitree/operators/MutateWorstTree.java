package gpalta.multitree.operators;

import gpalta.ops.TreeOperator;
import gpalta.multitree.MultiTreeIndividual;
import gpalta.core.TempVectorFactory;
import gpalta.core.ProblemData;
import gpalta.core.Common;

/**
 * Created by IntelliJ IDEA. User: nvn Date: 21-03-2007 Time: 04:21:00 PM To change this template
 * use File | Settings | File Templates.
 */
public class MutateWorstTree extends LowLevelMultiTreeOperator
{
    public MutateWorstTree(TreeOperator op)
    {
        super(op);
    }

    public boolean operate(MultiTreeIndividual[] individuals, TempVectorFactory tempVectorFactory, ProblemData problemData)
    {
        int t1 = selectWorstTreeRoulette(individuals[0]);
        treeOp.mutateBuild(individuals[0].getTree(t1));
        return true;
    }

    public int nIndividuals()
    {
        return 1;
    }
}
