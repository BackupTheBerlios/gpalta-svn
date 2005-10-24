/*
 * Or.java
 *
 * Created on 23 de mayo de 2005, 11:50 PM
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
public class Or extends Node
{
    
    public double eval(Evolution evo)
    {
        return ( kids[0].eval(evo)!=0 || kids[1].eval(evo)!=0 ? 1:0 );
    }

    public void evalVect(Evolution evo, double[] outVect)
    {
        kids[0].evalVect(evo, outVect);
        double[] resultKid2 = evo.getEvalVector();
        kids[1].evalVect(evo, resultKid2);
        for (int i=0; i < evo.realDataHolder.nSamples; i++)
        {
            outVect[i] = ( outVect[i]!=0 || resultKid2[i]!=0 ? 1:0 );
        }
        evo.releaseEvalVector();
    }
    
    public int nKids()
    {
        return 2;
    }
    
    public NodeType typeOfKids(int whichKid)
    {
        return Types.logic;
    }

    public String name()
    {
        return "or";
    }
    
}