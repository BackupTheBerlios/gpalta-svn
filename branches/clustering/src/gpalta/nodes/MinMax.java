package gpalta.nodes;

import gpalta.core.DataHolder;
import gpalta.core.TempOutputFactory;
import gpalta.core.Output;

/**
 * Created by IntelliJ IDEA.
 * User: nvn
 * Date: 05-08-2006
 * Time: 12:25:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class MinMax extends Node
{
    public double eval(DataHolder data)
    {
        return Math.min(getKid(0).eval(data), getKid(1).eval(data));
    }

    public void evalVect(Output out, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        assert out.getDim() == 2;
        Output outKid1 = tempOutputFactory.get();
        getKid(0).evalVect(outKid1, tempOutputFactory, data);
        Output outKid2 = tempOutputFactory.get();
        getKid(1).evalVect(outKid2, tempOutputFactory, data);
        double[] mins = out.getArray(0);
        double[] maxs = out.getArray(1);
        double[] outVectKid1 = outKid1.getArray(0);
        double[] outVectKid2 = outKid2.getArray(0);

        for (int wSample=0; wSample<data.nSamples; wSample++)
        {
            mins[wSample] = Math.min(outVectKid1[wSample], outVectKid2[wSample]);
        }

        outVectKid1 = outKid1.getArray(1);
        outVectKid2 = outKid2.getArray(1);
        for (int wSample=0; wSample<data.nSamples; wSample++)
        {
            maxs[wSample] = Math.max(outVectKid1[wSample], outVectKid2[wSample]);
        }
    }

    public void evalVect(double[] outVect, double[][] kidOutVect, DataHolder data)
    {

    }

    public int nKids()
    {
        return 2;
    }

    public String name()
    {
        return "minmax";
    }
}
