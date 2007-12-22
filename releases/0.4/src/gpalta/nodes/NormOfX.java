/*
 * NormOfX.java
 *
 * Created on 22-06-2007, 08:01:29 PM
 *
 * Copyright (C) 2007 Neven Boric <nboric@gmail.com>
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

import gpalta.core.ProblemData;

public class NormOfX extends Node
{

    public double eval(ProblemData problemData)
    {
        return problemData.getNormCurrentSample();
    }

    protected void evalVectInternal(double[] outVect, double[][] kidsOutput, ProblemData problemData)
    {
        System.arraycopy(problemData.getNorms(), 0, outVect, 0, problemData.nSamples);
    }

    public int nKids()
    {
        return 0;
    }

    public String name()
    {
        return "normx";
    }
}
