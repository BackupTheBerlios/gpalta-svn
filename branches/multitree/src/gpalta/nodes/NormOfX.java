package gpalta.nodes;

import gpalta.core.ProblemData;
import gpalta.core.Common;

/**
 * Created by IntelliJ IDEA. User: nvn Date: 22-06-2007 Time: 08:01:29 PM To change this template
 * use File | Settings | File Templates.
 */
public class NormOfX extends Node
{

    public double eval(ProblemData problemData)
    {
        return problemData.getNormCurrentSample();
    }

    protected void evalVectInternal(double[] outVect, double[][] kidsOutput, ProblemData problemData)
    {
        System.arraycopy(problemData.getNorms(), 0, outVect, 0, problemData.nSamples);
    }

    public int nKids()
    {
        return 0;
    }

    public String name()
    {
        return "normx";
    }
}
