/*
 * LogicAnd.java
 *
 * Created on 10 de mayo de 2005, 11:33 PM
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
public class And extends Node
{
    
    public double eval(Evolution evo)
    {
        return ( kids[0].eval(evo)!=0 && kids[1].eval(evo)!=0 ? 1:0 );
    }
    
    public void evalVect(Evolution evo, double[] outVect)
    {
        kids[0].evalVect(evo, outVect);
        double[] resultKid2 = evo.getEvalVector();
        kids[1].evalVect(evo, resultKid2);
        for (int i=0; i < evo.dataHolder.nSamples; i++)
        {
            outVect[i] = ( outVect[i]!=0 && resultKid2[i]!=0 ? 1:0 );
        }
        evo.releaseEvalVector();
    }
    
    public int nKids()
    {
        return 2;
    }
    
    public NodeType typeOfKids(NodeTypesConfig types, int whichKid)
    {
        return types.logic;
    }
    
    public String name()
    {
        return "and";
    }
    
}
