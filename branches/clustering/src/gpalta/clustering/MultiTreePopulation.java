/*
 * MultiTreePopulation.java
 *
 * Created on 6 de junio de 2006, 04:09 PM
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

package gpalta.clustering;

import gpalta.core.*;
import gpalta.core.Tree;
import gpalta.nodes.*;
import gpalta.ops.*;
import java.util.*;

/**
 *
 * @author neven
 */
public class MultiTreePopulation implements Population
{
    private List<GroupedTree> treeList;
    private List<TreeGroup> treeGroups;
    private Config config;
    private Output out;

    public void eval(Fitness f, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        for (GroupedTree t : treeList)
        {
            t.setFitness(0);
            if (!config.rememberLastEval || !t.fitCalculated)
            {
                getOutput(t, t.out, evalVectors, data, prev);
                t.fitCalculated = true;
            }
        }
        for (TreeGroup g : treeGroups)
        {
            for (int i=0; i<config.nClasses; i++)
            {
                out.setArray(i, g.get(i).out.getArray(0));
            }
            f.calculate(out, g, evalVectors, data, prev);
        }
        
    }
    
    private void getOutput(Tree t, Output o, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        double[] results = o.getArray(0);
        data.reset();
        prev.reset();
        if (config.nPreviousOutput == 0 && config.useVect)
        {
            t.evalVect(results, evalVectors, data, prev);
        }
        else
        {
            for (int i=0; i<data.nSamples; i++)
            {
                results[i] = t.eval(data, prev);
                data.update();
                prev.update(results[i]);
            }
        }
        sigmoid(results);
    }
    
    private void sigmoid(double[] x)
    {
        for (int i = 0; i < x.length; i++)
        {
            x[i] = 1/(1 + Math.exp(-x[i]));
        }
    }

    public Output getRawOutput(Individual ind, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        Output out = new Output(config.nClasses, data.nSamples);
        TreeGroup ind2 = (TreeGroup)ind;
        for (int i=0; i<config.nClasses; i++)
        {
            if (!ind2.get(i).fitCalculated)
            {
                getOutput(ind2.get(i), ind2.get(i).out, evalVectors, data, prev);
            }
            else
            {
                out.setArray(i, ind2.get(i).out.getArrayCopy(0));
            }
        }
        return out;
    }
    
    public Output getProcessedOutput(Individual ind, Fitness f, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        Output raw = getRawOutput(ind, evalVectors, data, prev);
        Output processed = f.getProcessedOutput(raw, ind, evalVectors, data, prev);
        return processed;
    }

    public Individual get(int which)
    {
        return treeGroups.get(which);
    }

    public void init(Config config, DataHolder data, TreeBuilder builder)
    {
        this.config = config;
        treeGroups = new ArrayList<TreeGroup>(config.populationSize);
        for (int i=0; i<config.populationSize; i++)
        {
            treeGroups.add(new TreeGroup(config.nClasses));
        }
        treeList = new ArrayList<GroupedTree>(config.nTrees);
        for (int i=0; i<config.populationSize; i++)
        {
            GroupedTree t = new GroupedTree(builder.treeRoot());
            t.out = new Output(1, data.nSamples);
            treeList.add(t);
        }
        builder.build(treeList);
        asignTrees(treeList, treeGroups);
        out = new Output(config.nClasses, data.nSamples);
    }

    public void doSelection(TreeSelector sel)
    {
        treeList = sel.select(treeList);
        asignTrees(treeList, treeGroups);
    }

    public void evolve(TreeOperator op)
    {
        op.operate(treeList);
    }
    
    private void asignTrees(List<GroupedTree> trees, List<TreeGroup> groups)
    {
        int treePointer = 0;
        int[] perm = Common.randPerm(config.nTrees);
        for (TreeGroup g : groups)
        {
            for (int i=0; i<config.nClasses; i++)
            {
                //if a tree didn't get selected, its isOnPop will be false
                if (g.get(i) == null || !g.get(i).isOnPop())
                {
                    //treeList.get(perm[treePointer]).groups.add(g);
                    g.set(i, treeList.get(perm[treePointer]));
                    if (++treePointer == config.nTrees)
                    {
                        treePointer = 0;
                        perm = Common.randPerm(config.nTrees);
                    }
                }
            }
        }
    }
    
}
