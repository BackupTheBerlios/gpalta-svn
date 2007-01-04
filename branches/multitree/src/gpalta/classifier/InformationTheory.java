package gpalta.classifier;

/**
 * Created by IntelliJ IDEA. User: nvn Date: 15-12-2006 Time: 09:07:08 PM To change this template
 * use File | Settings | File Templates.
 */
public abstract class InformationTheory
{
    public static double[] frequencies(double[] x, double[] possibleValues)
    {
        double[] count = new double[possibleValues.length];
        for (int i=0; i<x.length; i++)
        {
            for (int j=0; j<possibleValues.length; j++)
            {
                if (x[i] == possibleValues[j])
                {
                    count[j]++;
                    break;
                }
            }
        }
        for (int j=0; j<possibleValues.length; j++)
        {
            if (count[j] != 0)
                count[j] /= x.length;
        }
        double[] px = new double[x.length];
        for (int i=0; i<x.length; i++)
        {
            for (int j=0; j<possibleValues.length; j++)
            {
                if (x[i] == possibleValues[j])
                {
                    px[i] = count[j];
                    break;
                }
            }
        }
        return px;
    }

    public static double[] px(double[] x, double[] possibleValues)
    {
        double[] count = new double[possibleValues.length];
        for (int i=0; i<x.length; i++)
        {
            for (int j=0; j<possibleValues.length; j++)
            {
                if (x[i] == possibleValues[j])
                {
                    count[j]++;
                    break;
                }
            }
        }
        for (int j=0; j<possibleValues.length; j++)
        {
            if (count[j] != 0)
                count[j] /= x.length;
        }
        return count;
    }

    public static double[][] pxy(double[] x, double[] y, double[] possibleValues)
    {
        if (x.length != y.length)
            return null;
        double[][] count = new double[possibleValues.length][possibleValues.length];
        int pv = possibleValues.length;
        for (int i=0; i<pv; i++)
        {
            for (int j=0; j<pv; j++)
            {
                for (int k=0; k<x.length; k++)
                {
                    if(x[k]==possibleValues[i] && y[k]==possibleValues[j])
                    {
                        count[i][j]++;
                    }
                }
            }
        }
        for (int i=0; i<pv; i++)
        {
            for (int j=0; j<pv; j++)
            {
                count[i][j] /= x.length;
            }
        }
        return count;
    }

    public static double gaussianKernel(double[] x, double sigma)
    {
        double k = x.length;
        return 1/(Math.pow((2*Math.PI), k/2)*Math.pow(sigma,k))*Math.exp(-innerProduct(x)/(2*sigma*sigma));
    }

    public static double innerProduct(double[] x)
    {
        double out=0;
        for (int i=0; i<x.length; i++)
        {
            out += x[i]*x[i];
        }
        return out;
    }

    public static double gaussianKernel(double x, double sigma)
    {
        return 1/(Math.sqrt(2*Math.PI)*sigma)*Math.exp(-x*x/(2*sigma*sigma));
    }

    public static double informationPotencial(double[] x, double sigma)
    {
        int n=x.length;
        double out = 0;
        for (int i=0; i<n; i++)
        {
            for (int j=0; j<n; j++)
            {
                out += gaussianKernel(x[i]-x[j], Math.sqrt(2)*sigma);
            }
        }
        return out/(n*n);
    }

    public static double mutualInformation(double[] px, double[] py, double[][] pxy)
    {
        double out = 0;
        for (int i=0; i<px.length; i++)
        {
            for (int j=0; j<px.length; j++)
            {
                if (pxy[i][j] != 0)
                    out += pxy[i][j]*Math.log(pxy[i][j]/(px[i]*py[j]));
            }
        }
        return out/Math.log(2);
    }
    
}
