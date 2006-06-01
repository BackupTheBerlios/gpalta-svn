/*
 * Output.java
 *
 * Created on 31 de mayo de 2006, 11:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gpalta.core;

/**
 *
 * @author neven
 */
public class Output
{
    private double[][] data;
    private int nArrays;
    private int nSamples;
    
    /** Creates a new instance of Output */
    public Output(int nArrays, int nSamples)
    {
        data = new double[nArrays][nSamples];
        this.nArrays = nArrays;
        this.nSamples = nSamples;
    }
    
    public double[] getArray(int which)
    {
        return data[which];
    }
    
    public void setArray(int which, double[] array)
    {
        data[which] = array;
    }
    
    public int nArrays()
    {
        return nArrays;
    }
    
}
