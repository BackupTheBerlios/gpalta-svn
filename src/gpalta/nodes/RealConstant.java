/*
 * RealConstant.java
 *
 * Created on 11 de mayo de 2005, 12:17 AM
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
public class RealConstant extends Node
{
    
    private double constant;
    
    public double eval(Evolution evo)
    {
        return ( constant );
    }
    
    public void evalVect(Evolution evo, double[] outVect)
    {
        for (int i=0; i < evo.dataHolder.nSamples; i++)
        {
            outVect[i] = constant;
        }
    }
    
    public String name()
    {
        return ("" + constant);
    }
    
    public void init()
    {
        double random01 = Common.globalRandom.nextDouble();
        this.constant = Config.constLowLimit + (Config.constUpLimit-Config.constLowLimit)*random01;
    }
}
