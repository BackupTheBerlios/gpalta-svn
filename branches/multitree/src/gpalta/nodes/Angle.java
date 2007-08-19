package gpalta.nodes;

import gpalta.core.ProblemData;

/**
 * Created by IntelliJ IDEA. User: nvn Date: 22-06-2007 Time: 08:01:29 PM To change this template
 * use File | Settings | File Templates.
 */
public class Angle extends Node
{

    private int wAngle;
    public Angle(int wAngle)
    {
        this.wAngle = wAngle;
    }

    public double eval(ProblemData problemData)
    {
        return problemData.getAngleCurrentSample(wAngle);
    }

    protected void evalVectInternal(double[] outVect, double[][] kidsOutput, ProblemData problemData)
    {
        System.arraycopy(problemData.getAngles(wAngle), 0, outVect, 0, problemData.nSamples);
    }

    public int nKids()
    {
        return 0;
    }

    public String name()
    {
        return "angle" + wAngle;
    }
}