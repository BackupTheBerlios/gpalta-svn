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

/**
 * @author neven
 */
public class Tree extends Individual implements NodeParent

{
    public double hr0;
    public double hr1;
    public boolean fitCalculated;
    private NodeSet type;
    private Node[] kids;

    /**
     * Creates a new instance of Tree
     */
    public Tree(NodeSet type)
    {
        this.type = type;
        newKids();
    }

    public double eval(DataHolder data, PreviousOutputHolder prev)
    {
        return getKid(0).eval(data, prev);
    }

    public void evalVect(double[] outVect, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        getKid(0).evalVect(outVect, evalVectors, data, prev);
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
        return getKid(0).toString();
    }

    public Tree(String expression, NodeSet type, NodeFactory nodeFactory)
    {
        this.type = type;
        newKids();
        Node.parse(expression, this, 0, nodeFactory);
        getKid(0).setParent(this);
    }

    public Individual deepClone()
    {
        Tree out = null;
        try
        {
            out = (Tree) super.clone();
            out.newKids();
            out.setKid(0, getKid(0).deepClone(0));
        }
        catch (CloneNotSupportedException e)
        {
            Logger.log(e);
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

    public int getNSubNodes()
    {
        return getKid(0).getNSubNodes() + 1;
    }

    public void setNSubNodes(int nSubNodes)
    {
    }

    public Node getKid(int whichKid)
    {
        return kids[whichKid];
    }

    public void setKid(int whichKid, Node kid)
    {
        kids[whichKid] = kid;
    }

    public int getMaxDepthFromHere()
    {
        return 1 + getKid(0).getMaxDepthFromHere();
    }

    public void setMaxDepthFromHere(int maxDepthFromHere)
    {
    }

    public int getCurrentDepth()
    {
        return -1;
    }

    public void newKids()
    {
        kids = new Node[nKids()];
    }

}
