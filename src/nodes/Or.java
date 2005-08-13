/*
 * Or.java
 *
 * Created on 23 de mayo de 2005, 11:50 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package nodes;
import java.util.*;
import GPalta.*;

/**
 *
 * @author neven
 */
public class Or extends LogicNode
{
    
    public boolean eval(Evolution evo)
    {
        return ( ((LogicNode)kids[0]).eval(evo) || ((LogicNode)kids[1]).eval(evo) );
    }
    
    public boolean[] evalVect(Evolution evo)
    {
        boolean[] resultKid1 = ((LogicNode)kids[0]).evalVect(evo);
        boolean[] resultKid2 = ((LogicNode)kids[1]).evalVect(evo);
        boolean[] out = new boolean[evo.realDataHolder.nSamples];
        for (int i=0; i < evo.realDataHolder.nSamples; i++)
        {
            out[i] = resultKid1[i] || resultKid2[i];
        }
        return out;
    }
    
    public int nKids()
    {
        return 2;
    }
    
    public List<Node> typeOfKids()
    {
        return Types.logicAny;
    }
    
    public String name()
    {
        return "or";
    }
    
}