/*
 * RealAdd.java
 *
 * Created on 10 de mayo de 2005, 11:23 PM
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
public class Plus extends RealNode
{

    public double eval(Evolution evo)
    {
        return ( ((RealNode)kids[0]).eval(evo) + ((RealNode)kids[1]).eval(evo) );
    }
    
    public void evalVect(Evolution evo, double[] outVect)
    {
        double[] resultKid1 = evo.getRealEvalVector();
        ((RealNode)kids[0]).evalVect(evo, resultKid1);
        double[] resultKid2 = evo.getRealEvalVector();
        ((RealNode)kids[1]).evalVect(evo, resultKid2);
        for (int i=0; i < evo.realDataHolder.nSamples; i++)
        {
            outVect[i] = resultKid1[i] + resultKid2[i];
        }
        evo.releaseRealEvalVector();
        evo.releaseRealEvalVector();
    }
    
    public int nKids()
    {
        return 2;
    }
    
    public List<Node> typeOfKids()
    {
        return Types.realAny;
    }
    
    public String name()
    {
        return "plus";
    }
    
}
