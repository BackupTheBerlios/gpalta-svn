package gpalta.core;

/**
 * Created by IntelliJ IDEA. User: nvn Date: 03-01-2007 Time: 11:39:17 PM To change this template
 * use File | Settings | File Templates.
 */
public class SingleOutput extends Output
{
    public final  double[] x;

    public SingleOutput(int nSamples)
    {
        x = new double[nSamples];
    }
    
    public int getDim()
    {
        return 1;
    }

    public void store(double[] vector)
    {
        System.arraycopy(vector, 0, x, 0, x.length);
    }
}
