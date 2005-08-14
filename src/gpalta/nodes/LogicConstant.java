/*
 * LogicConstant.java
 *
 * Created on 11 de mayo de 2005, 11:54 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package gpalta.nodes;
import gpalta.core.*;

/**
 *
 * @author neven
 */
public class LogicConstant extends LogicNode
{
    
    private boolean constant;
    
    public boolean eval(Evolution evo)
    {
        return ( constant );
    }
    
    public boolean[] evalVect(Evolution evo)
    {
        boolean[] out = new boolean[evo.realDataHolder.nSamples];
        for (int i=0; i < evo.realDataHolder.nSamples; i++)
        {
            out[i] = constant;
        }
        return out;
    }
    
    public String name()
    {
        return ("" + constant);
    }
    
    public void init()
    {
        int bit = Common.globalRandom.nextInt(2);
        if (bit == 0)
            constant = false;
        else
            constant = true;
    }
    
}
