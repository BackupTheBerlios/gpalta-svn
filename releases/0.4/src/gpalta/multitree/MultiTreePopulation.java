/*
 * MultiTreePopulation.java
 *
 * Created on 03-01-2007, 11:08:05 PM
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

import gpalta.core.*;
import gpalta.ops.TreeBuilder;
import gpalta.ops.IndSelector;
import gpalta.ops.TreeOperator;
import gpalta.multitree.operators.MultiTreeOperator;
import gpalta.multitree.operators.LowLevelMultiTreeOperator;
import gpalta.multithread.MultiThreadedEvaluator;

import java.util.List;
import java.util.ArrayList;

/**
 * A Population composed of MultiTreeIndividuals.
 */
public class MultiTreePopulation implements Population
{

    private List<MultiTreeIndividual> multiTreeList;
    private Config config;
    private MultiOutput outputs;
    private MultiTreeOperator multiTreeOp;
    private LowLevelMultiTreeOperator[] operatorsApplied;
    double[] oldFitness;


    public void eval(MultiThreadedEvaluator evaluator, Fitness f, TempVectorFactory tempVectorFactory, ProblemData problemData, int[] wSamples)
    {
        evaluator.eval(multiTreeList, outputs, wSamples);
        /*
        for (MultiTreeIndividual ind : multiTreeList)
        {
            ind.evalVect(outputs, tempVectorFactory, problemData);
            double[] fit = f.calculate(outputs, ind, problemData, wSamples);
            f.assign(ind, fit);
        }
        */
//        if (operatorsApplied != null)
//        {
//            boolean evalOnSame = false;
//            if (!evalOnSame)
//            {
//                for (int i=0; i<multiTreeOp.lowLevelOps.length; i++)
//                {
//                    multiTreeOp.lowLevelOps[i].timesApplied = 0;
//                    multiTreeOp.lowLevelOps[i].timesFitnessImproved = 0;
//                }
//
//                for (int i=0; i<multiTreeList.size(); i++)
//                {
//                    //Trees at the end of the list might not get an operator applied:
//                    if (operatorsApplied[i] != null)
//                    {
//                        operatorsApplied[i].timesApplied++;
//                        if (multiTreeList.get(i).readFitness() > oldFitness[i])
//                            operatorsApplied[i].timesFitnessImproved++;
//                    }
//                }
//            }
//            else
//            {
//                int nDone = 0;
//                for (int i=0; i<multiTreeOp.lowLevelOps.length; i++)
//                {
//                    LowLevelMultiTreeOperator op = multiTreeOp.lowLevelOps[i];
//                    for (int j=0; j<op.nIndividuals(); j++)
//                    {
//                        op.timesApplied ++;
//                        if (multiTreeList.get(nDone+j).readFitness() > oldFitness[j])
//                        {
//                            op.timesFitnessImproved++;
//                        }
//                    }
//                    nDone += op.nIndividuals();
//                }
//            }
//        }
    }

    private void getOutput(MultiTreeIndividual mt, MultiOutput output, TempVectorFactory tempVectorFactory, ProblemData problemData)
    {
        if (config.useVect)
        {
            mt.evalVect(output, tempVectorFactory, problemData);
        }
        else
        {
            problemData.reset();
            for (int i=0; i< problemData.nSamples; i++)
            {
                MultiOutput o = (MultiOutput)mt.eval(problemData);
                problemData.update();
                for (int j=0; j<mt.nTrees(); j++)
                {
                    output.getArray(j)[i] = o.getArray(j)[0];
                }
            }
        }
    }

    public Output getRawOutput(Individual ind, TempVectorFactory tempVectorFactory, ProblemData problemData)
    {
        MultiOutput out = new MultiOutput(((MultiTreeIndividual)ind).nTrees(), problemData.nSamples);
        getOutput((MultiTreeIndividual) ind, out, tempVectorFactory, problemData);
        return out;
    }

    public Output getProcessedOutput(Individual ind, Fitness f, TempVectorFactory tempVectorFactory, ProblemData problemData)
    {
        Output raw = getRawOutput(ind, tempVectorFactory, problemData);
        return f.getProcessedOutput(raw, problemData);
    }

    public Individual get(int which)
    {
        return multiTreeList.get(which);
    }

    public void init(Config config, ProblemData problemData, TreeBuilder builder, TreeOperator treeOp)
    {
        this.config = config;
        multiTreeList = new ArrayList<MultiTreeIndividual>(config.populationSize);
        List<Tree> tmpList = new ArrayList<Tree>(config.populationSize*config.nClasses);
        for (int i = 0; i < config.populationSize; i++)
        {
            multiTreeList.add(new MultiTreeIndividual(config.nClasses));
            for (int j=0; j<config.nClasses; j++)
            {
                ((MultiTreeIndividual)get(i)).setTree(j, new Tree(builder.treeRoot()));
                tmpList.add(((MultiTreeIndividual)get(i)).getTree(j));
            }
        }
        builder.build(tmpList);
        outputs = new MultiOutput(config.nClasses, problemData.nSamples);
        multiTreeOp = new MultiTreeOperator(config, treeOp);
    }

    public void doSelection(IndSelector sel)
    {
        multiTreeList = sel.select(multiTreeList);
    }

    public void evolve(TreeOperator treeOp, TempVectorFactory tempVectorFactory, ProblemData problemData)
    {
//        oldFitness = new double[multiTreeList.size()];
//        for (int i=0; i<multiTreeList.size(); i++)
//            oldFitness[i] = multiTreeList.get(i).readFitness();
//        operatorsApplied = multiTreeOp.operate(multiTreeList, tempVectorFactory, problemData);
//
        multiTreeOp.operate(multiTreeList, tempVectorFactory, problemData);
    }

    public LowLevelMultiTreeOperator[] getOperatorStats()
    {
        return multiTreeOp.lowLevelOps;
    }
}
