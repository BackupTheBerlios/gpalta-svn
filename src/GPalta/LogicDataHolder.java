/*
 * LogicDataHolder.java
 *
 * Created on 30 de mayo de 2005, 06:58 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package GPalta;

/**
 *
 * @author DSP
 */
public abstract class LogicDataHolder 
{
    public static int nDelays;
    private static boolean[] prevOut;
    private static int index;
    
    public static void init()
    {
        nDelays = Config.nPreviousOutput;
        prevOut = new boolean[nDelays];
        for (int i=0; i<nDelays; i++)
        {
            prevOut[i] = false;
        }
        index = 0;
    }
    
    public static void reset()
    {
        index = 0;
        for (int i=0; i<nDelays; i++)
        {
            prevOut[i] = false;
        }
    }
    
    public static void update(boolean currentOut)
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
    
    public static boolean getData(int delay)
    {
        int tmpIndex = index - (delay - 1);
        if (tmpIndex < 0)
        {
            tmpIndex = nDelays + tmpIndex;
        }
        return prevOut[tmpIndex];
    }
}
