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
    private int nTreesPerClass;
    private int n;

    public void eval(Fitness f, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            for (BufferedTree t : treeList[wClass])
            {
                t.setFitness(0);
                if (!config.rememberLastEval || !t.fitCalculated)
                {
                    getOutput(t, t.getOutput(), tempOutputFactory, data);
                    t.fitCalculated = true;
                }
            }
        }

        for (TreeGroup g : treeGroups)
        {
            for (int i = 0; i < config.nClasses; i++)
            {
                out.setArray(i, g.getTree(i).getOutput().getArray(0));
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
        Output out = new Output(config.nClasses, 0);
        Output tmpOut = new Output(1, data.nSamples);
        TreeGroup ind2 = (TreeGroup) ind;
        for (int i = 0; i < config.nClasses; i++)
        {
            getOutput(ind2.getTree(i), tmpOut, tempOutputFactory, data);
            out.setArray(i, tmpOut.getArrayCopy(0));
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

        nTreesPerClass = config.nTrees/config.nClasses;
        if (config.selectionMethod.equals("tournament"))
            nTreesPerClass += (config.nTrees/config.nClasses)%config.tournamentSize;

        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            treeList[wClass] = new ArrayList<BufferedTree>(nTreesPerClass);
            for (int i = 0; i < nTreesPerClass; i++)
            {
                BufferedTree t = new BufferedTree(builder.treeRoot());
                t.setOutput(new Output(1, data.nSamples));
                //t.groupList = new ArrayList<TreeGroup>();
                treeList[wClass].add(t);
            }
            builder.build(treeList[wClass]);

        }
        asignTrees(treeList, treeGroups);

        out = new Output(config.nClasses, data.nSamples);
    }

    public void doSelection(IndSelector sel)
    {
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            treeList[wClass] = sel.select(treeList[wClass]);
        }
        if (config.autoNClusters)
        {
            if (++n == 2)
            {
                treeGroups = sel.select(treeGroups, false);
                n = 0;
            }
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
        for (int wClass=0; wClass<config.nClasses; wClass++)
        {
            for (BufferedTree t : treeList[wClass])
            {
                t.nGroups = 0;
            }
        }

        for (TreeGroup g : groups)
        {
            for (int i = 0; i < g.nTrees(); i++)
            {
                if (g.getTree(i) != null)
                    g.getTree(i).nGroups++;
            }
        }

        int[][] perm = new int[config.nClasses][];
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
