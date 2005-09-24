/*
 * RealVar.java
 *
 * Created on 12 de mayo de 2005, 01:24 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package gpalta.nodes;
import java.util.*;
import gpalta.core.*;

/**
 * Real variable terminal
 *
 * @author neven
 */
public class RealVar extends RealNode
{
    public int whichVar;
    
    /** Creates a new instance of RealVar */
    public RealVar(int whichVar) 
    {
        this.whichVar = whichVar;
    }
    
    public double eval(Evolution evo)
    {
        return evo.realDataHolder.getData(whichVar);
    }
    
    public void evalVect(Evolution evo, double[] outVect)
    {
        System.arraycopy(evo.realDataHolder.getDataVect(whichVar), 0, outVect, 0, evo.realDataHolder.nSamples);
    }
    
    public String name()
    {
        return ("X" + whichVar);
    }

}