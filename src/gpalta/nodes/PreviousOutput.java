/*
 * PreviousOutput.java
 *
 * Created on 30 de mayo de 2005, 07:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package gpalta.nodes;

import gpalta.core.*;

/**
 *
 * @author DSP
 */
public class PreviousOutput extends Node
{

    private int delay;
    public PreviousOutput(int delay)
    {
        this.delay = delay;
    }
    
    
    public double eval(Evolution evo)
    {
        return evo.previousOutputHolder.getData(delay);
    }
    
    /**
     * This should never be called. The whole evalVect was implemented to be
     * used when there are no previous outputs as Nodes
     */
    public void evalVect(Evolution evo, double[] outVect)
    {
        //TODO: Maybe throw an exception?
        Logger.log("Error: Should not be calling PreviousOutput's evalVect()");
    }
    
    public String name()
    {
        return ("Yn_" + delay);
    }
    
}
