package gpalta.nodes;

import gpalta.core.ProblemData;

/**
 * Created by IntelliJ IDEA. User: nvn Date: 28-06-2007 Time: 09:21:09 PM To change this template
 * use File | Settings | File Templates.
 */
public class Abs extends Node
{
    public double eval(ProblemData problemData)
    {
        return Math.abs(getKid(0).eval(problemData));
    }

    public void evalVectInternal(double[] outVect, double[][] kidsOutput, ProblemData problemData)
    {
        for (int wSample=0; wSample<outVect.length; wSample++)
        {
            outVect[wSample] = Math.abs(kidsOutput[0][wSample]);
        }
    }

    public int nKids()
    {
        return 1;
    }

    public String name()
    {
        return "abs";
    }
}
