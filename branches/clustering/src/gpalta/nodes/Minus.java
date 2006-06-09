/*
 * Minus.java
 *
 * Created on 23 de mayo de 2005, 11:47 PM
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
public class Minus extends Node
{

    public double eval(DataHolder data, PreviousOutputHolder prev)
    {
        return ( getKids()[0].eval(data, prev) - getKids()[1].eval(data, prev) );
    }
    
    public void evalVect(double[] outVect, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        getKids()[0].evalVect(outVect, evalVectors, data, prev);
        double[] resultKid2 = evalVectors.get();
        getKids()[1].evalVect(resultKid2, evalVectors, data, prev);
        for (int i=0; i < data.nSamples; i++)
        {
            outVect[i] = outVect[i] - resultKid2[i];
        }
        evalVectors.release();
    }
    
    public int nKids()
    {
        return 2;
    }
    
    public String name()
    {
        return "minus";
    }
    
}
