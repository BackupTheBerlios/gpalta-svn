/*
 * RealVar.java
 *
 * Created on 12 de mayo de 2005, 01:24 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package nodes;
import java.util.*;
import GPalta.*;

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
    
    public double eval()
    {
        return RealDataHolder.getData(whichVar);
    }
    
    public double[] evalVect()
    {
        return RealDataHolder.getDataVect(whichVar);
    }
    
    public String name()
    {
        return ("X" + whichVar);
    }

}