/*
 * Tree.java
 *
 * Created on 13 de mayo de 2005, 09:32 PM
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
public class Tree extends Node

{
    public double fitness;
    public double hr0;
    public double hr1;
    public boolean fitCalculated;
    public boolean isOnPop;
    List<Node> type;
    
    /**
     * Creates a new instance of Tree 
     */
    public Tree(List<Node> type)
    {
        this.type = type;
    }
    
    public double eval(Evolution evo)
    {
        return kids[0].eval(evo);
    }
    
    public void evalVect(Evolution evo, double[] outVect)
    {
        kids[0].evalVect(evo, outVect);
    }
    
    public int nKids()
    {
        return 1;
    }
    
    public List<Node> typeOfKids()
    {
        return type;
    }
    public List<Node> typeOfTerminalKids()
    {
        Logger.log("Error: Should not be calling Tree's typeOfTerminalKids()");
        return null;
    }
    public List<Node> typeOfFunctionKids()
    {
        Logger.log("Error: Should not be calling Tree's typeOfFunctionKids");
        return null;
    }
    
    public String toString()
    {
        return kids[0].toString();
    }
    
    public String name()
    {
        return "TREE";
    }
    
}
