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
public class ReplaceWorstWithBest extends LowLevelMultiTreeOperator
{
    public ReplaceWorstWithBest(TreeOperator op)
    {
        super(op);
    }

    public boolean operate(MultiTreeIndividual[] individuals, TempVectorFactory tempVectorFactory, ProblemData problemData)
    {
        int t1 = selectWorstTreeRoulette(individuals[0]);
        int t2 = selectBestTreeRoulette(individuals[1]);

        individuals[0].setTree(t1, (Tree)individuals[1].getTree(t2).deepClone());
        return true;
    }

    public int nIndividuals()
    {
        return 2;
    }
}
