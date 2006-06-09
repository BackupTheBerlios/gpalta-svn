/*
 * Nodo.java
 *
 * Created on 10 de mayo de 2005, 09:54 PM
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
import java.io.Serializable;
import gpalta.core.*;
import java.util.*;

/**
 * Generic Node definition
 * 
 *
 * @author neven
 */
public abstract class Node implements NodeParent, Cloneable, Serializable
{
    
    private NodeParent parent;
    private Node[] kids;
    
    private int whichKidOfParent;
    
    private int currentDepth;
    private int maxDepthFromHere;
    private int nSubNodes;
    
    private NodeSet[] kidsType;
    
    public void init(Config config)
    {

    }
    
    public abstract double eval(DataHolder data, PreviousOutputHolder prev);
    public abstract void evalVect(double[] outVect, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev);
    
    public void setTypeOfKids(int whichKid, NodeSet t)
    {
        if (kidsType == null)
            kidsType = new NodeSet[nKids()];
        kidsType[whichKid] = t;
    }
    
    public NodeSet typeOfKids(int whichKid)
    {
        return kidsType[whichKid];
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
            out += " " + getKids()[i];
        }
        if (nKids() > 0)
        {
            out += ")";
        }
        return out;
    }
    
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
        
        out.setCurrentDepth(currentDepth);
        
        if (nKids()>0)
        {
            out.setKids(new Node[nKids()]);
        }
        
        for (int i=0; i<nKids(); i++)
        {
            out.getKids()[i] = getKids()[i].deepClone(currentDepth + 1);
            out.getKids()[i].setParent(out);
        }
        return out;
    }

    public static String parse(String expression, NodeParent parent, int whichKid, NodeFactory nodeFactory)
    {
        java.util.regex.Pattern op = java.util.regex.Pattern.compile("[a-zA-Z_0-9\\.]+");
        java.util.regex.Matcher matcher = op.matcher(expression);
        if (matcher.find())
        {
            parent.getKids()[whichKid] = nodeFactory.newNode(matcher.group(), parent.getCurrentDepth()+1);
            parent.getKids()[whichKid].parent = parent;
        }
        else
        {
            //error
        }
        
        expression = expression.substring(matcher.end()+1).trim();
        
        int nKidsDone = 0;
        while (expression.length() != 0)
        {
            if(expression.startsWith(")"))
            {
                expression = expression.substring(1);
                if (nKidsDone < parent.getKids()[whichKid].nKids())
                {
                    //error
                }
                break;
            }
            else
            {
                if (nKidsDone == 0)
                    parent.getKids()[whichKid].kids = new Node[parent.getKids()[whichKid].nKids()];
                if (expression.startsWith("("))
                {
                    expression = parse(expression, parent.getKids()[whichKid], nKidsDone, nodeFactory).trim();
                }
                else
                {
                    matcher = op.matcher(expression);
                    if (matcher.find())
                    {
                        parent.getKids()[whichKid].kids[nKidsDone] = nodeFactory.newNode(matcher.group(), parent.getKids()[whichKid].currentDepth+1);
                        parent.getKids()[whichKid].kids[nKidsDone].parent = parent.getKids()[whichKid];
                        parent.getKids()[whichKid].nSubNodes += 1;
                        parent.getKids()[whichKid].maxDepthFromHere = Math.max(parent.getKids()[whichKid].maxDepthFromHere, 1);
                    }
                    else
                    {
                        //error
                    }
                    expression = expression.substring(matcher.end()).trim();
                }
            }
            nKidsDone++;
        }
        if (nKidsDone < parent.getKids()[whichKid].nKids())
        {
            //error
        }
        
        parent.setNSubNodes(1 + parent.getKids()[whichKid].getNSubNodes());
        parent.setMaxDepthFromHere(Math.max(parent.getMaxDepthFromHere(), 1 + parent.getKids()[whichKid].getMaxDepthFromHere()));
        
        return expression;
    }

    public int getWhichKidOfParent()
    {
        return whichKidOfParent;
    }

    public void setWhichKidOfParent(int whichKidOfParent)
    {
        this.whichKidOfParent = whichKidOfParent;
    }

    public NodeParent getParent()
    {
        return parent;
    }

    public void setParent(NodeParent parent)
    {
        this.parent = parent;
    }

    public int getNSubNodes()
    {
        return nSubNodes;
    }

    public void setNSubNodes(int nSubNodes)
    {
        this.nSubNodes = nSubNodes;
    }

    public Node[] getKids()
    {
        return kids;
    }
    
    public void setKids(Node[] kids)
    {
        this.kids = kids;
    }

    public int getCurrentDepth()
    {
        return currentDepth;
    }

    public void setCurrentDepth(int currentDepth)
    {
        this.currentDepth = currentDepth;
    }

    public int getMaxDepthFromHere()
    {
        return maxDepthFromHere;
    }

    public void setMaxDepthFromHere(int maxDepthFromHere)
    {
        this.maxDepthFromHere = maxDepthFromHere;
    }
    
}
