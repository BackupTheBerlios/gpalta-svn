/*
 * LogicGreaterThan.java
 *
 * Created on 10 de mayo de 2005, 11:50 PM
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
public class GreaterThan extends Node
{

    public double eval(Evolution evo)
    {
        return ( kids[0].eval(evo) > kids[1].eval(evo) ? 1:0 );
    }
    
    public void evalVect(Evolution evo, double[] outVect)
    {
        double[] resultKid1 = evo.getEvalVector();
        kids[0].evalVect(evo, resultKid1);
        double[] resultKid2 = evo.getEvalVector();
        kids[1].evalVect(evo, resultKid2);
        for (int i=0; i < evo.realDataHolder.nSamples; i++)
        {
            outVect[i] = ( resultKid1[i] > resultKid2[i] ? 1:0 );
        }
        evo.releaseEvalVector();
        evo.releaseEvalVector();
    }
    
    public int nKids()
    {
        return 2;
    }
    
    public NodeType typeOfKids()
    {
        return Types.real;
    }
    
    public String name()
    {
        return "gt";
    }
    
}
