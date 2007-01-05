/*
 * SingleTreePopulation.java
 *
 * Created on 31 de mayo de 2006, 05:16 PM
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

package gpalta.core;

import gpalta.core.Tree;
import gpalta.ops.*;

import java.util.*;
import java.io.Serializable;

/**
 * Implements the most common population type on GP: a fixed number of trees, each interpreded
 * separatedly
 *
 * @author neven
 */
public class SingleTreePopulation implements Population, Serializable
{
    private List<Tree> treeList;
    private Config config;
    private SingleOutput outputs;

    public void eval(Fitness f, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        for (Tree t : treeList)
        {
            if (!config.rememberLastEval || !t.fitCalculated)
            {
                getOutput(t, outputs, tempOutputFactory, data);
                f.calculate(outputs, t, tempOutputFactory, data);
                t.fitCalculated = true;
            }
        }
    }

    private void getOutput(Tree t, SingleOutput o, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        double[] results = o.x;
        data.reset();
        if (config.useVect)
        {
            t.evalVect(o, tempOutputFactory, data);
        }
        else
        {
            for (int i = 0; i < data.nSamples; i++)
            {
                results[i] = ((SingleOutput)t.eval(data)).x[0];
                data.update();
            }
        }
    }

    public Output getRawOutput(Individual ind, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        SingleOutput out = new SingleOutput(data.nSamples);
        getOutput((Tree) ind, out, tempOutputFactory, data);
        return out;
    }

    public Output getProcessedOutput(Individual ind, Fitness f, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        Output raw = getRawOutput(ind, tempOutputFactory, data);
        return f.getProcessedOutput(raw, ind, tempOutputFactory, data);
    }

    public Individual get(int which)
    {
        return treeList.get(which);
    }

    public void doSelection(IndSelector sel)
    {
        treeList = sel.select(treeList);
    }

    public void evolve(TreeOperator op)
    {
        op.operate(treeList);
    }

    public void init(Config config, DataHolder data, TreeBuilder builder)
    {
        this.config = config;
        treeList = new ArrayList<Tree>(config.populationSize);
        for (int i = 0; i < config.populationSize; i++)
        {
            treeList.add(new Tree(builder.treeRoot()));
        }
        builder.build(treeList);
        outputs = new SingleOutput(data.nSamples);
    }

}
