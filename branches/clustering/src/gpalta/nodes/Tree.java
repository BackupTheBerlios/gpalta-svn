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

package gpalta.nodes;
import java.util.*;
import gpalta.core.*;

/**
 *
 * @author neven
 */
public class Tree extends Node implements Individual

{
    public double fitness;
    public double hr0;
    public double hr1;
    public boolean fitCalculated;
    public boolean isOnPop;
    NodeSet type;
    
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
    
    public String name()
    {
        return "TREE";
    }
    
    public Tree (String expression, NodeSet type, NodeFactory nodeFactory)
    {
        this.type = type;
        currentDepth = -1;
        kids = new Node[1];
        Node.parse(expression, this, 0, nodeFactory);
        kids[0].parent = this;
        nSubNodes = 1 + kids[0].nSubNodes;
        maxDepthFromHere = 1 + kids[0].maxDepthFromHere;
    }
    
}
