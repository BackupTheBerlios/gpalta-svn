/*
 * MultiTreeOperator.java
 *
 * Created on 04-01-2007, 01:38:40 AM
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

package gpalta.multitree;

import gpalta.core.Tree;
import gpalta.core.Common;
import gpalta.core.Config;
import gpalta.ops.TreeOperator;

import java.util.List;

public class MultiTreeOperator
{
    private TreeOperator treeOp;
    private Config config;

    public MultiTreeOperator(Config config, TreeOperator treeOp)
    {
        this.treeOp = treeOp;
        this.config = config;
    }

    public <M extends MultiTreeIndividual> void operate(List<M> population)
    {
        int[] perm = Common.randPerm(population.size());
        double op;
        for (int i = 0; i < population.size(); i++)
        {
            op = Common.globalRandom.nextDouble();
            if (op <= config.upLimitProbCrossOver)
            {
                //Do cross over, except for the last tree:
                if (i != population.size() - 1)
                {
                    int t1 = Common.globalRandom.nextInt(population.get(perm[i]).nTrees());
                    int t2 = Common.globalRandom.nextInt(population.get(perm[i+1]).nTrees());
                    if (treeOp.crossOver(population.get(perm[i]).getTree(t1), population.get(perm[i+1]).getTree(t2)))
                    {
                        population.get(perm[i]).fitCalculated = false;
                        population.get(perm[i + 1]).fitCalculated = false;
                        i++;
                    }
                }
            }
            else if (op <= config.upLimitProbMutation)
            {
                int t1 = Common.globalRandom.nextInt(population.get(perm[i]).nTrees());
                population.get(perm[i]).fitCalculated = false;
                treeOp.mutateBuild(population.get(perm[i]).getTree(t1));
            }
            else
            {
                //Do nothing (reproduction)
            }
        }
    }
}
