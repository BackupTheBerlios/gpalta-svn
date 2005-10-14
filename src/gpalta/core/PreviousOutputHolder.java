/*
 * LogicDataHolder.java
 *
 * Created on 30 de mayo de 2005, 06:58 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package gpalta.core;

/**
 *
 * @author DSP
 */
public class PreviousOutputHolder 
{
    public int nDelays;
    private double[] prevOut;
    private int index;
    
    public PreviousOutputHolder()
    {
        nDelays = Config.nPreviousOutput;
        prevOut = new double[nDelays];
        for (int i=0; i<nDelays; i++)
        {
            prevOut[i] = 0;
        }
        index = 0;

        Logger.log("Using " + Config.nPreviousOutput + " previous outputs as " + (Config.usePreviousOutputAsReal ? "real" : "logic") + " terminals");
        
    }
    
    public void reset()
    {
        index = 0;
        for (int i=0; i<nDelays; i++)
        {
            prevOut[i] = 0;
        }
    }
    
    public void update(double currentOut)
    {
        if (nDelays != 0)
        {
            index++;
            if (index == nDelays)
            {
                index = 0;
            }
            prevOut[index] = currentOut;
        }
    }
    
    public double getData(int delay)
    {
        int tmpIndex = index - (delay - 1);
        if (tmpIndex < 0)
        {
            tmpIndex = nDelays + tmpIndex;
        }
        return prevOut[tmpIndex];
    }
}
