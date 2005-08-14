/*
 * EvolutionStats.java
 *
 * Created on 26 de mayo de 2005, 01:59 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package gpalta.core;
import gpalta.nodes.*;



/**
 * Holds some statistics about the ongoing Evolution
 * @author neven
 */
public class EvolutionStats
{
    public Tree bestSoFar;
    public boolean bestTreeChanged = true;
    public double bestFitThisGen;
    public int generation;
    public double avgFit;
    public double avgNodes;
    
}
