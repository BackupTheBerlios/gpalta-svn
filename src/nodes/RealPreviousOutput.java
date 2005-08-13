/*
 * RealPreviousOutput.java
 *
 * Created on 22 de julio de 2005, 05:50 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package nodes;

import GPalta.*;

/**
 * Real representation of previous outputs
 * @author DSP
 */
public class RealPreviousOutput extends RealNode
{
    
    private int delay;
    public RealPreviousOutput(int delay)
    {
        this.delay = delay;
    }
    
    public double eval(Evolution evo)
    {
        if (evo.logicDataHolder.getData(delay))
            return 1;
        else
            return 0;
    }
    
    /**
     * This should never be called. The whole evalVect was implemented to be
     * used when there are no previous outputs as Nodes
     */
    public double[] evalVect(Evolution evo)
    {
        //TODO: Maybe throw an exception?
        Logger.log("Error: Should not be calling RealPreviousOutput's evalVect()");
        return null;
    }
    
    public String name()
    {
        return ("Yn_" + delay);
    }
    
}
