/*
 * DistanceToCentroid.java
 *
 * Created on 03-08-2006, 12:08:15 PM
 *
 * Copyright (C) 2006 Neven Boric <nboric@gmail.com>
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

public class DistanceToCentroid extends Node
{
    public double[] c;

    public void init(Config config, DataHolder data)
    {
        c = new double[data.nVars];
        for (int wVar=0; wVar<data.nVars; wVar++)
            c[wVar] = data.getMin(wVar+1) + Common.globalRandom.nextDouble() * (data.getMax(wVar+1)-data.getMin(wVar+1));
    }

    public double eval(DataHolder data)
    {
        double[] x = data.getCurrentSample();
        return Common.dist2(x,c);
    }

    public void evalVect(double[] outVect, double[][] kidsOutput, DataHolder data)
    {
        double[] x;
        for (int wSample=0; wSample<outVect.length; wSample++)
        {
            x = data.getSample(wSample);
            outVect[wSample] = 0;
            for (int wVar=0; wVar<data.nVars; wVar++)
                outVect[wSample] += Math.abs(c[wVar]-x[wVar])/data.getRange(wVar+1);
            outVect[wSample] /= data.nVars;
        }
    }

    public int nKids()
    {
        return 0;
    }

    public String name()
    {
        return "cent";
    }
}
