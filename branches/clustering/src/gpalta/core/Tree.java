/*
 * Tree.java
 *
 * Created on 13 de mayo de 2005, 09:32 PM
 *
 * Copyright (C) 2005, 2006 Neven Boric <nboric@gmail.com>
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

package gpalta.core;
import gpalta.nodes.*;
import java.util.*;

/**
 *
 * @author neven
 */
public class Tree extends Individual implements NodeParent, Cloneable

{
    public double hr0;
    public double hr1;
    public boolean fitCalculated;
    NodeSet type;
    private Node[] kids;
    
    /**
     * Creates a new instance of Tree 
     */
    public Tree(NodeSet type)
    {
        this.type = type;
    }
    
    public double eval(DataHolder data, PreviousOutputHolder prev)
    {
        return kids[0].eval(data, prev);
    }
    
    public void evalVect(double[] outVect, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        kids[0].evalVect(outVect, evalVectors, data, prev);
    }
    
    public int nKids()
    {
        return 1;
    }
    
    public NodeSet typeOfKids(int whichKid)
    {
        return type;
    }

    public String toString()
    {
        return kids[0].toString();
    }
    
    public Tree (String expression, NodeSet type, NodeFactory nodeFactory)
    {
        this.type = type;
        kids = new Node[1];
        Node.parse(expression, this, 0, nodeFactory);
        kids[0].setParent(this);
    }
    
    public Individual deepClone()
    {
        Tree out = null;
        try
        {
            out = (Tree)super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            Logger.log(e);
        }
        
        if (nKids()>0)
        {
            out.setKids(new Node[nKids()]);
        }
        
        for (int i=0; i<nKids(); i++)
        {
            out.getKids()[i] = getKids()[i].deepClone(0);
            out.getKids()[i].setParent(out);
        }
        return out;
    }
    
    public int getSize()
    {
        return getNSubNodes();
    }

    public NodeParent getParent()
    {
        return null;
    }

    public void setParent(NodeParent n)
    {
    }

    public int getWhichKidOfParent()
    {
        return -1;
    }

    public void setWhichKidOfParent(int whichKidOfParent)
    {
    }

    public int getNSubNodes()
    {
        return kids[0].getNSubNodes() + 1;
    }

    public void setNSubNodes(int nSubNodes)
    {
    }

    public Node[] getKids()
    {
        return kids;
    }

    public void setKids(Node[] kids)
    {
        this.kids = kids;
    }
    
    public int getMaxDepthFromHere()
    {
        return 1 + kids[0].getMaxDepthFromHere();
    }

    public void setMaxDepthFromHere(int maxDepthFromHere)
    {
    }

    public int getCurrentDepth()
    {
        return -1;
    }

    public void setCurrentDepth(int currentDepth)
    {
    }
    
}
