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
public class Reproduce extends LowLevelMultiTreeOperator
{
    public Reproduce(TreeOperator op)
    {
        super(op);
    }

    public boolean operate(MultiTreeIndividual[] individuals, TempVectorFactory tempVectorFactory, ProblemData problemData)
    {
        //do nothing
        return false;
    }

    public int nIndividuals()
    {
        return 1;
    }
}
