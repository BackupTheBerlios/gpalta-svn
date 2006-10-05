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
 * @author neven
 */
public class IfThenElse extends Node
{

    public double eval(DataHolder data)
    {
        if (getKid(0).eval(data) != 0)
        {
            return getKid(1).eval(data);
        }
        else
        {
            return getKid(2).eval(data);
        }
    }

    public void evalVect(double[] outVect, double[][] kidOutVect, DataHolder data)
    {
        for (int wSample=0; wSample<outVect.length; wSample++)
        {
            outVect[wSample] = (kidOutVect[0][wSample] !=0 ? kidOutVect[1][wSample] : kidOutVect[2][wSample]);
        }
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
