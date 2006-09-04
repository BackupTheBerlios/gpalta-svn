package gpalta.nodes;

import gpalta.core.*;

/**
 * Created by IntelliJ IDEA.
 * User: nvn
 * Date: 03-08-2006
 * Time: 12:08:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class DistanceToCentroid extends Node
{
    public double[] c;

    public void init(Config config, DataHolder data)
    {
        c = new double[data.nVars];
        for (int wVar=0; wVar<data.nVars; wVar++)
            c[wVar] = data.getMin(wVar+1) + Common.globalRandom.nextDouble() * (data.getMax(wVar+1)-data.getMin(wVar+1));
    }

    public double eval(DataHolder data)
    {
        double[] x = data.getAllVars();
        return Common.dist2(x,c);
    }

    public void evalVect(double[] outVect, double[][] kidOutVect, DataHolder data)
    {
        double[] x;
        for (int wSample=0; wSample<outVect.length; wSample++)
        {
            x = data.getAllVars(wSample);
            outVect[wSample] = 0;
            for (int wVar=0; wVar<data.nVars; wVar++)
                outVect[wSample] += Math.abs(c[wVar]-x[wVar])/data.getRange(wVar+1);
            outVect[wSample] /= data.nVars;
        }
    }

    public int nKids()
    {
        return 0;
    }

    public String name()
    {
        return "cent";
    }
}
