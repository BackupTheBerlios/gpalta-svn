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
    
    public void evalVect(Evolution evo, boolean[] outVect)
    {
        boolean[] resultKid1 = evo.getLogicEvalVector();
        ((LogicNode)kids[0]).evalVect(evo, resultKid1);
        boolean[] resultKid2 = evo.getLogicEvalVector();
        ((LogicNode)kids[1]).evalVect(evo, resultKid2);
        for (int i=0; i < evo.realDataHolder.nSamples; i++)
        {
            outVect[i] = resultKid1[i] && resultKid2[i];
        }
        evo.releaseLogicEvalVector();
        evo.releaseLogicEvalVector();
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
