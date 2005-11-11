/*
 * Tree.java
 *
 * Created on 13 de mayo de 2005, 09:32 PM
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
    NodeType type;
    
    /**
     * Creates a new instance of Tree 
     */
    public Tree(NodeType type)
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
    
    public NodeType typeOfKids(NodeTypesConfig types, int whichKid)
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