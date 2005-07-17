/*
 * LogicAnd.java
 *
 * Created on 10 de mayo de 2005, 11:33 PM
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
public class And extends LogicNode
{
    
    public boolean eval()
    {
        return ( ((LogicNode)kids[0]).eval() && ((LogicNode)kids[1]).eval() );
    }
    
    public boolean[] evalVect()
    {
        boolean[] resultKid1 = ((LogicNode)kids[0]).evalVect();
        boolean[] resultKid2 = ((LogicNode)kids[1]).evalVect();
        boolean[] out = new boolean[RealDataHolder.nSamples];
        for (int i=0; i < RealDataHolder.nSamples; i++)
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
