/*
 * CrossRandomWithMostSimilar.java
 *
 * Created on 21-03-2007, 04:21:00 PM
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

package gpalta.multitree.operators;

import gpalta.ops.TreeOperator;
import gpalta.multitree.MultiTreeIndividual;
import gpalta.core.TempVectorFactory;
import gpalta.core.ProblemData;
import gpalta.core.Common;

public class CrossRandomWithMostSimilar extends LowLevelMultiTreeOperator
{
    public CrossRandomWithMostSimilar(TreeOperator op)
    {
        super(op);
    }

    public boolean operate(MultiTreeIndividual[] individuals, TempVectorFactory tempVectorFactory, ProblemData problemData)
    {
        int t1 = Common.globalRandom.nextInt(individuals[0].nTrees());
        int t2 = selectMostSimilarTreeRoulette(individuals[0].getTree(t1), individuals[1], tempVectorFactory, problemData);
        return treeOp.crossOver(individuals[0].getTree(t1), individuals[1].getTree(t2));
    }

    public int nIndividuals()
    {
        return 2;
    }
}
