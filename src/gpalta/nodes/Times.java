/*
 * Times.java
 *
 * Created on 23 de mayo de 2005, 11:49 PM
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
public class Times extends Node
{

    public double eval(Evolution evo)
    {
        return ( kids[0].eval(evo) * kids[1].eval(evo) );
    }
    
    public void evalVect(Evolution evo, double[] outVect)
    {
        kids[0].evalVect(evo, outVect);
        double[] resultKid2 = evo.getEvalVector();
        kids[1].evalVect(evo, resultKid2);
        for (int i=0; i < evo.dataHolder.nSamples; i++)
        {
            outVect[i] = outVect[i] * resultKid2[i];
        }
        evo.releaseEvalVector();
    }
    
    public int nKids()
    {
        return 2;
    }
    
    public NodeType typeOfKids(int whichKid)
    {
        return Types.real;
    }
 
    public String name()
    {
        return "times";
    }
    
}
