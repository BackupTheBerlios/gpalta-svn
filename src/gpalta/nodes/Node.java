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

    public static String parse(String expression, Node parent, int whichKid, NodeFactory nodeFactory)
    {
        java.util.regex.Pattern op = java.util.regex.Pattern.compile("[a-zA-Z_0-9\\.]+");
        java.util.regex.Matcher matcher = op.matcher(expression);
        if (matcher.find())
        {
            parent.kids[whichKid] = nodeFactory.newNode(matcher.group(), parent.currentDepth+1);
            parent.kids[whichKid].parent = parent;
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
                if (nKidsDone < parent.kids[whichKid].nKids())
                {
                    //error
                }
                break;
            }
            else
            {
                if (nKidsDone == 0)
                    parent.kids[whichKid].kids = new Node[parent.kids[whichKid].nKids()];
                if (expression.startsWith("("))
                {
                    expression = parse(expression, parent.kids[whichKid], nKidsDone, nodeFactory).trim();
                }
                else
                {
                    matcher = op.matcher(expression);
                    if (matcher.find())
                    {
                        parent.kids[whichKid].kids[nKidsDone] = nodeFactory.newNode(matcher.group(), parent.kids[whichKid].currentDepth+1);
                        parent.kids[whichKid].kids[nKidsDone].parent = parent.kids[whichKid];
                        parent.kids[whichKid].nSubNodes += 1;
                        parent.kids[whichKid].maxDepthFromHere = Math.max(parent.kids[whichKid].maxDepthFromHere, 1);
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
        if (nKidsDone < parent.kids[whichKid].nKids())
        {
            //error
        }
        
        parent.nSubNodes = 1 + parent.kids[whichKid].nSubNodes;
        parent.maxDepthFromHere = Math.max(parent.maxDepthFromHere, 1 + parent.kids[whichKid].maxDepthFromHere);
        
        return expression;
    }
    
}
