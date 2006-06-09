/*
 * SingleTreePopulation.java
 *
 * Created on 31 de mayo de 2006, 05:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gpalta.core;

import gpalta.core.Tree;
import gpalta.ops.*;
import java.util.*;

/**
 *
 * @author neven
 */
public class SingleTreePopulation implements Population
{
    private List<Tree> treeList;
    private Config config;
    private Output outputs;

    public void eval(Fitness f, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        for (Tree t : treeList)
        {
            if (!config.rememberLastEval || !t.fitCalculated)
            {
                getOutput(t, outputs, evalVectors, data, prev);
                f.calculate(outputs, t, evalVectors, data, prev);
                t.fitCalculated = true;
            }
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
    }

    public Output getRawOutput(Individual ind, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        Output out = new Output(1, data.nSamples);
        getOutput((Tree)ind, out, evalVectors, data, prev);
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
        return treeList.get(which);
    }

    public void doSelection(TreeSelector sel)
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
        for (int i=0; i<config.populationSize; i++)
        {
            treeList.add(new Tree(builder.treeRoot()));
        }
        builder.build(treeList);
        outputs = new Output(1, data.nSamples);
    }
    
}