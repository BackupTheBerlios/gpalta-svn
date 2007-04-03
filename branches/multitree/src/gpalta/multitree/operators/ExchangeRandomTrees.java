package gpalta.multitree.operators;

import gpalta.ops.TreeOperator;
import gpalta.multitree.MultiTreeIndividual;
import gpalta.core.TempVectorFactory;
import gpalta.core.ProblemData;
import gpalta.core.Common;
import gpalta.core.Tree;

/**
 * Created by IntelliJ IDEA. User: nvn Date: 21-03-2007 Time: 04:21:00 PM To change this template
 * use File | Settings | File Templates.
 */
public class ExchangeRandomTrees extends LowLevelMultiTreeOperator
{
    public ExchangeRandomTrees(TreeOperator op)
    {
        super(op);
    }

    public boolean operate(MultiTreeIndividual[] individuals, TempVectorFactory tempVectorFactory, ProblemData problemData)
    {
        int t1 = Common.globalRandom.nextInt(individuals[0].nTrees());
        int t2 = Common.globalRandom.nextInt(individuals[1].nTrees());
        Tree tree1 = individuals[0].getTree(t1);
        Tree tree2 = individuals[1].getTree(t2);
        individuals[0].setTree(t1, tree2);
        individuals[1].setTree(t2, tree1);
        return true;
    }

    public int nIndividuals()
    {
        return 2;
    }
}
