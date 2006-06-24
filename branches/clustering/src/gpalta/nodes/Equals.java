/*
 * Equals.java
 *
 * Created on 28 de marzo de 2006, 10:44 PM
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
 * @author neven
 */
public class Equals extends Node
{

    public double eval(DataHolder data, PreviousOutputHolder prev)
    {
        return (getKid(0).eval(data, prev) == getKid(1).eval(data, prev) ? 1 : 0);
    }

    public void evalVect(double[] outVect, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        getKid(0).evalVect(outVect, evalVectors, data, prev);
        double[] resultKid2 = evalVectors.get();
        getKid(1).evalVect(resultKid2, evalVectors, data, prev);
        for (int i = 0; i < data.nSamples; i++)
        {
            outVect[i] = (outVect[i] == resultKid2[i] ? 1 : 0);
        }
        evalVectors.release();
    }

    public int nKids()
    {
        return 2;
    }

    public String name()
    {
        return "eq";
    }
}
