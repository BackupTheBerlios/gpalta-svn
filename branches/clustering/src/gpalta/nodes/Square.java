package gpalta.nodes;

import gpalta.core.DataHolder;
import gpalta.core.TempOutputFactory;
import gpalta.core.Output;

/**
 * Created by IntelliJ IDEA.
 * User: nvn
 * Date: 02-08-2006
 * Time: 10:49:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class Square extends Node
{
    public double eval(DataHolder data)
    {
        return Math.pow(getKid(0).eval(data), 2);
    }
    
    public void evalVect(double[] outVect, double[][] kidOutVect, DataHolder data)
    {
        for (int wSample=0; wSample<outVect.length; wSample++)
        {
            outVect[wSample] = Math.pow(kidOutVect[0][wSample], 2);
        }
    }

    public int nKids()
    {
        return 1;
    }

    public String name()
    {
        return "sq";
    }
}
