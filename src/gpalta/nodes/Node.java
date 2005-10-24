/*
 * Nodo.java
 *
 * Created on 10 de mayo de 2005, 09:54 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package gpalta.nodes;
import java.io.Serializable;
import gpalta.core.*;
import java.util.*;

/**
 * Generic Node definition
 * 
 *
 * @author neven
 */
public abstract class Node implements Cloneable, Serializable
{
    
    public Node parent;
    public Node[] kids;
    
    public int whichKidOfParent;
    
    public int currentDepth;
    public int maxDepthFromHere;
    
    public void init()
    {

    }
    
    public abstract double eval(Evolution evo);
    public abstract void evalVect(Evolution evo, double[] outVect);
    
    public NodeType typeOfKids(int whichKid)
    {
        Logger.log("This shouldn' happen. A class that extends Node must implement" +
                "its own typeOfKids() if it has any");
        return null;
    }
    
    public int nKids()
    {
        return 0;
    }
    
    public abstract String name();
    
    public String toString()
    {
        String out = "";
        if (nKids() > 0)
        {
            out += "(";
        }
        out += name();
        for (int i=0; i<nKids(); i++)
        {
            out += " " + kids[i];
        }
        if (nKids() > 0)
        {
            out += ")";
        }
        return out;
    }
    
    public int nSubNodes;
    
    public Object clone() throws CloneNotSupportedException
    {
        return (Node)super.clone();
    }
    
    public Node deepClone(int currentDepth)
    {
        
        Node out = null;
        try
        {
            out = (Node)super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            Logger.log(e);
        }
        
        out.currentDepth = currentDepth;
        
        if (nKids()>0)
        {
            out.kids = new Node[nKids()];
        }
        
        for (int i=0; i<nKids(); i++)
        {
            out.kids[i] = kids[i].deepClone(currentDepth + 1);
            out.kids[i].parent = out;
        }
        return out;
    }

    
}
