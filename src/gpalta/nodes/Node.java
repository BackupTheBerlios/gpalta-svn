/*
 * Nodo.java
 *
 * Created on 10 de mayo de 2005, 09:54 PM
 *
 * Copyright (C) 2005  Neven Boric <nboric@gmail.com>
 *
 * This file is part of GPalta.
 *
 * GPalta is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * GPalta is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPalta; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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
    
    public void init(Config config)
    {

    }
    
    public abstract double eval(Evolution evo);
    public abstract void evalVect(Evolution evo, double[] outVect);
    
    public NodeType typeOfKids(NodeTypesConfig types, int whichKid)
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
