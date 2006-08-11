package gpalta.nodes;

import gpalta.core.DataHolder;
import gpalta.core.TempOutputFactory;
import gpalta.core.Output;

/**
 * Created by IntelliJ IDEA.
 * User: nvn
 * Date: 02-08-2006
 * Time: 10:37:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class Exponential extends Node
{
    public double eval(DataHolder data)
    {
        return Math.exp(getKid(0).eval(data));
    }

    public void evalVect(double[] outVect, double[][] kidOutVect, DataHolder data)
    {
        for (int wSample=0; wSample<outVect.length; wSample++)
        {
            outVect[wSample] = Math.exp(kidOutVect[0][wSample]);
        }
    }

    public String name()
    {
        return "exp";  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int nKids()
    {
        return 1;
    }
}
