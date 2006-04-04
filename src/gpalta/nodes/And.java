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
    
    public double eval(DataHolder data, PreviousOutputHolder prev)
    {
        /* 
         * Note that this might not evaluate the second kid. This is useful to
         * speed things up, but could cause problems if some node on that branch
         * has side effects
         */
        return ( kids[0].eval(data, prev)!=0 && kids[1].eval(data, prev)!=0 ? 1:0 );
    }
    
    public void evalVect(double[] outVect, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        kids[0].evalVect(outVect, evalVectors, data, prev);
        double[] resultKid2 = evalVectors.get();
        kids[1].evalVect(resultKid2, evalVectors, data, prev);
        for (int i=0; i < data.nSamples; i++)
        {
            outVect[i] = ( outVect[i]!=0 && resultKid2[i]!=0 ? 1:0 );
        }
        evalVectors.release();
    }
    
    public int nKids()
    {
        return 2;
    }
    
    public String name()
    {
        return "and";
    }
    
}
