package gpalta.multitree.operators;

import gpalta.ops.TreeOperator;
import gpalta.multitree.MultiTreeIndividual;
import gpalta.multitree.MultiOutput;
import gpalta.core.TempVectorFactory;
import gpalta.core.ProblemData;
import gpalta.core.SingleOutput;
import gpalta.core.Tree;

/**
 * Created by IntelliJ IDEA. User: nvn Date: 21-03-2007 Time: 04:21:00 PM To change this template
 * use File | Settings | File Templates.
 */
public class ReplaceWorstWithMostSimilarIfBetter extends LowLevelMultiTreeOperator
{
    public ReplaceWorstWithMostSimilarIfBetter(TreeOperator op)
    {
        super(op);
    }

    public boolean operate(MultiTreeIndividual[] individuals, TempVectorFactory tempVectorFactory, ProblemData problemData)
    {
        int t1 = selectWorstTree(individuals[0]);
        int t2 = selectMostSimilarTree(individuals[0].getTree(t1), individuals[1], tempVectorFactory, problemData);
        

        if (individuals[1].getTree(t2).readFitness() > individuals[0].getTree(t1).readFitness())
        {
            individuals[0].setTree(t1, (Tree)individuals[1].getTree(t2).deepClone());
            return true;
        }
        return false;
    }

    public int nIndividuals()
    {
        return 2;
    }
}
