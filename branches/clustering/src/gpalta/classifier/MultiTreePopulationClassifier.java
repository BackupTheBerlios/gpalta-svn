package gpalta.classifier;

import gpalta.core.*;
import gpalta.clustering.BufferedTree;
import gpalta.clustering.TreeGroup;
import gpalta.ops.TreeBuilder;
import gpalta.ops.IndSelector;
import gpalta.ops.TreeOperator;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA. User: nvn Date: 24-08-2006 Time: 02:31:24 PM To change this template
 * use File | Settings | File Templates.
 */
public class MultiTreePopulationClassifier implements Population, Serializable
{
    private List<BufferedTree>[] treeList;
    private List<TreeGroup> treeGroups;
    private Config config;
    private Output out;

    public void eval(Fitness f, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            for (BufferedTree t : treeList[wClass])
            {
                t.setFitness(0);
                if (!config.rememberLastEval || !t.fitCalculated)
                {
                    getOutput(t, t.out, tempOutputFactory, data);
                    t.fitCalculated = true;
                }
            }
        }

        for (TreeGroup g : treeGroups)
        {
            for (int i = 0; i < config.nClasses; i++)
            {
                for (int wDim=0; wDim<config.outputDimension; wDim++)
                    out.setArray(i*config.outputDimension+wDim, g.getTree(i).out.getArray(wDim));
            }
            f.calculate(out, g, tempOutputFactory, data);
        }

    }

    private void getOutput(Tree t, Output o, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        double[] results = o.getArray(0);
        data.reset();
        if (config.useVect)
        {
            t.evalVect(o, tempOutputFactory, data);
        }
        else
        {
            for (int i = 0; i < data.nSamples; i++)
            {
                results[i] = t.eval(data);
                data.update();
            }
        }
        Common.sigmoid(results);
    }

    public Output getRawOutput(Individual ind, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        Output out = new Output(config.nClasses * config.outputDimension, data.nSamples);
        TreeGroup ind2 = (TreeGroup) ind;
        for (int i = 0; i < config.nClasses; i++)
        {
            assert ind2.getTree(i).fitCalculated;
            for (int wDim=0; wDim<config.outputDimension; wDim++)
                out.setArray(i*config.outputDimension+wDim, ind2.getTree(i).out.getArrayCopy(wDim));
        }
        return out;
    }

    public Output getProcessedOutput(Individual ind, Fitness f, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        Output raw = getRawOutput(ind, tempOutputFactory, data);
        return f.getProcessedOutput(raw, ind, tempOutputFactory, data);
    }

    public Individual get(int which)
    {
        return treeGroups.get(which);
    }

    public void init(Config config, DataHolder data, TreeBuilder builder)
    {
        this.config = config;
        treeGroups = new ArrayList<TreeGroup>(config.populationSize);
        for (int i = 0; i < config.populationSize; i++)
        {
            treeGroups.add(new TreeGroup(config.nClasses));
        }
        treeList = new ArrayList[config.nClasses];
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            treeList[wClass] = new ArrayList<BufferedTree>(config.nTrees);
            for (int i = 0; i < config.nTrees/config.nClasses; i++)
            {
                BufferedTree t = new BufferedTree(builder.treeRoot());
                t.out = new Output(config.outputDimension, data.nSamples);
                //t.groupList = new ArrayList<TreeGroup>();
                treeList[wClass].add(t);
            }
            builder.build(treeList[wClass]);

        }
        asignTrees(treeList, treeGroups);

        out = new Output(config.nClasses * config.outputDimension, data.nSamples);
    }

    public void doSelection(IndSelector sel)
    {
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            treeList[wClass] = sel.select(treeList[wClass]);
        }
        asignTrees(treeList, treeGroups);
    }

    public void evolve(TreeOperator op)
    {
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            op.operate(treeList[wClass]);
        }
    }

    private void asignTrees(List<BufferedTree>[] trees, List<TreeGroup> groups)
    {

        int[][] perm = new int[config.nClasses][];
        int nTreesPerClass = config.nTrees/config.nClasses;
        for (int i = 0; i < config.nClasses; i++)
            perm[i] = Common.randPerm(nTreesPerClass);
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            int treePointer = 0;
            for (TreeGroup g : groups)
            {
                if (g.getTree(wClass) == null || !g.getTree(wClass).isOnPop())
                {
                    g.setTree(wClass, trees[wClass].get(perm[wClass][treePointer]));
                    trees[wClass].get(perm[wClass][treePointer]).nGroups++;
                    if (++treePointer == nTreesPerClass)
                    {
                        treePointer = 0;
                        perm[wClass] = Common.randPerm(nTreesPerClass);
                    }
                }
            }
        }
    }

}
