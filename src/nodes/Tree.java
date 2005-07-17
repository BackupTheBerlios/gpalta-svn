/*
 * Tree.java
 *
 * Created on 13 de mayo de 2005, 09:32 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package nodes;
import java.util.*;

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
    
    public boolean eval()
    {
        return ((LogicNode)kids[0]).eval();
    }
    
    public boolean[] evalVect()
    {
        return ((LogicNode)kids[0]).evalVect();
    }
    
    public int nKids()
    {
        return 1;
    }
    
    public List<Node> typeOfKids()
    {
        return type;
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
