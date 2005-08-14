/*
 * LogicAnd.java
 *
 * Created on 10 de mayo de 2005, 11:33 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package gpalta.nodes;
import java.util.*;
import gpalta.core.*;

/**
 *
 * @author neven
 */
public class And extends LogicNode
{
    
    public boolean eval(Evolution evo)
    {
        return ( ((LogicNode)kids[0]).eval(evo) && ((LogicNode)kids[1]).eval(evo) );
    }
    
    public boolean[] evalVect(Evolution evo)
    {
        boolean[] resultKid1 = ((LogicNode)kids[0]).evalVect(evo);
        boolean[] resultKid2 = ((LogicNode)kids[1]).evalVect(evo);
        boolean[] out = new boolean[evo.realDataHolder.nSamples];
        for (int i=0; i < evo.realDataHolder.nSamples; i++)
        {
            out[i] = resultKid1[i] && resultKid2[i];
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
        return "and";
    }
    
}
