package gpalta.clustering;

import gpalta.core.Common;

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

    public static double gaussianKernel(double[] x, double sigma2)
    {
        double k = x.length;
        return 1/Math.pow((2*Math.PI*sigma2), k/2)*Math.exp(-innerProduct(x)/(2*sigma2));
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
        double sigma2 = Math.sqrt(2)*sigma;
        double out = 0;
        for (int i=0; i<n; i++)
        {
            for (int j=0; j<n; j++)
            {
                out += gaussianKernel(x[i]-x[j], sigma2);
            }
        }
        return out/(n*n);
    }

    public static double entropyEstimator(double[] x, double sigma)
    {
        int n=x.length;
        double sigma2 = Math.sqrt(2)*sigma;
        double out = gaussianKernel(x[0], sigma2);
        for (int i=1; i<n; i++)
        {
            out += gaussianKernel(x[i]-x[i-1], sigma2);
        }
        return -Math.log(out/n)/Math.log(2);
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

    public static double sigmaOpt2(double[][] data)
    {
        double d = data.length;
        double n = data[0].length;
        double sigmax2 = 0;
        for (int i=0; i< d; i++)
        {
            sigmax2 += Common.variance(data[i]);
        }
        sigmax2 /= d;
        return sigmax2*Math.pow(4/(n*(2*d+1)), 2/(d + 4));
    }
    
}
