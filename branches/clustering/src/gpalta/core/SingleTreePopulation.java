/*
 * SingleTreePopulation.java
 *
 * Created on 31 de mayo de 2006, 05:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gpalta.core;

import gpalta.nodes.Tree;
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
    List<double[]> outputs;

    public void eval(Fitness f, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        double[] results = outputs.get(0);
        for (Tree t : treeList)
        {
            if (!config.rememberLastEval || !t.fitCalculated)
            {
                getOutput(t, results, evalVectors, data, prev);
                f.calculate(outputs, evalVectors, data, prev);
                t.fitCalculated = true;
            }
        }
    }
    
    private void getOutput(Tree t, double[] results, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
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

    public List<double[]> getOutput(Individual ind, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        double[] results = outputs.get(0);
        getOutput((Tree)ind, results, evalVectors, data, prev);
        return outputs;
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
        builder.build(treeList);
        outputs = new ArrayList<double[]>(1);
        outputs.add(new double[data.nSamples]);
    }
    
}
