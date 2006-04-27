/*
 * IfThenElse.java
 *
 * Created on 14 de enero de 2006, 10:30 PM
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
import gpalta.core.*;

/**
 *
 * @author neven
 */
public class IfThenElse extends Node
{
    
    public double eval(DataHolder data, PreviousOutputHolder prev)
    {
        if (kids[0].eval(data, prev) != 0)
        {
            return kids[1].eval(data, prev);
        }
        else
        {
            return kids[2].eval(data, prev);
        }
    }
    
    public void evalVect(double[] outVect, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        //Is there a way to not evaluate both kids?
        kids[0].evalVect(outVect, evalVectors, data, prev);
        double[] resultKid2 = evalVectors.get();
        kids[1].evalVect(resultKid2, evalVectors, data, prev);
        double[] resultKid3 = evalVectors.get();
        kids[2].evalVect(resultKid3, evalVectors, data, prev);
        for (int i=0; i < data.nSamples; i++)
        {
            outVect[i] = (outVect[i]!=0 ? resultKid2[i] : resultKid3[i]);
        }
        evalVectors.release();
        evalVectors.release();
    }
    
    public int nKids()
    {
        return 3;
    }

    public String name()
    {
        return "if";
    }
    
}
