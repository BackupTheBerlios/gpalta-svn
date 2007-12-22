/*
 * ClusteringOutput.java
 *
 * Created on 1 de junio de 2006, 05:52 PM
 *
 * Copyright (C) 2006, 2007 Neven Boric <nboric@gmail.com>
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

package gpalta.clustering;

import gpalta.multitree.MultiOutput;

public class ClusteringOutput extends MultiOutput
{
    public double[][] prototypes;
    public double[][] prob;

    public ClusteringOutput(int nArrays, int nSamples)
    {
        super(nArrays, nSamples);
    }

    public void setPertenenceCopy(double[][] prob)
    {
        this.prob = new double[prob.length][prob[0].length];
        for (int i = 0; i < prob.length; i++)
        {
            System.arraycopy(prob[i], 0, this.prob[i], 0, prob[i].length);
        }
    }

}
