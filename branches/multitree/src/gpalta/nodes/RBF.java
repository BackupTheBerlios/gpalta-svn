/*
 * RBF.java
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

import gpalta.clustering.InformationTheory;
import gpalta.core.Common;
import gpalta.core.Config;
import gpalta.core.ProblemData;

public class RBF extends Node
{
    private double[] c;
    private double sigmaFraction;
    private double[] d;

    public void init(Config config, ProblemData problemData)
    {
        c = new double[problemData.nVars];
        d = new double[problemData.nVars];
        sigmaFraction = 1/2 + 1.5*Common.globalRandom.nextDouble();
        for (int wVar=0; wVar< problemData.nVars; wVar++)
            c[wVar] = problemData.getMin(wVar+1) + Common.globalRandom.nextDouble() * (problemData.getMax(wVar+1)- problemData.getMin(wVar+1));
    }

    public double eval(ProblemData problemData)
    {
        double[] x = problemData.getCurrentSample();
        for (int wVar=0; wVar< problemData.nVars; wVar++)
            d[wVar] = x[wVar] - c[wVar];
        return InformationTheory.gaussianKernel(d, sigmaFraction*problemData.sigmaOpt2);
    }

    public void evalVectInternal(double[] outVect, double[][] kidsOutput, ProblemData problemData)
    {
        double[] x;
        for (int wSample=0; wSample<outVect.length; wSample++)
        {
            x = problemData.getSample(wSample);
            outVect[wSample] = 0;
            for (int wVar=0; wVar< problemData.nVars; wVar++)
                d[wVar] = x[wVar] - c[wVar];
            outVect[wSample] = InformationTheory.gaussianKernel(d, sigmaFraction*problemData.sigmaOpt2);
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
