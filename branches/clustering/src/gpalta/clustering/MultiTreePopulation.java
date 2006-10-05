package gpalta.clustering;

import gpalta.core.*;
import gpalta.ops.TreeBuilder;
import gpalta.ops.IndSelector;
import gpalta.ops.TreeOperator;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author neven
 */
public class MultiTreePopulation implements Population, Serializable
{
    private List<BufferedTree> treeList;
    private List<TreeGroup> treeGroups;
    private TreeGroupOperator tgo;
    private Config config;
    public double meanTreeFit;
    private int n;

    public void eval(Fitness f, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        for (BufferedTree t : treeList)
        {
            t.setFitness(0);
            if (!config.rememberLastEval || !t.fitCalculated)
            {
                getOutput(t, t.getOutput(), tempOutputFactory, data);
                t.fitCalculated = true;
            }
        }
        Output out = new Output(0,0);
        for (TreeGroup g : treeGroups)
        {
            if (out.getDim() != g.nTrees())
                out = new Output(g.nTrees(), 0);
            for (int i = 0; i < g.nTrees(); i++)
            {
                out.setArray(i, g.getTree(i).getOutput().getArray(0));
            }
            f.calculate(out, g, tempOutputFactory, data);
        }
        meanTreeFit = 0;
        for (BufferedTree t : treeList)
            meanTreeFit += t.readFitness();
        meanTreeFit /= config.nTrees;
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
        //Common.sigmoid(results);
    }

    public Output getRawOutput(Individual ind, TempOutputFactory tempOutputFactory, DataHolder data)
    {
        TreeGroup ind2 = (TreeGroup) ind;
        Output out = new Output(ind2.nTrees(), 0);
        Output tmpOut = new Output(1, data.nSamples);
        for (int i = 0; i < ind2.nTrees(); i++)
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
            treeGroups.add(new TreeGroup(2));
        }
        treeList = new ArrayList<BufferedTree>(config.nTrees);
        for (int i = 0; i < config.nTrees; i++)
        {
            BufferedTree t = new BufferedTree(builder.treeRoot());
            t.setOutput(new Output(1, data.nSamples));
            treeList.add(t);
        }
        builder.build(treeList);
        asignTrees(treeList, treeGroups);
        tgo = new TreeGroupOperator();
    }

    public void doSelection(IndSelector sel)
    {
        for (TreeGroup t : treeGroups)
            t.removeEmptyClusters();

        treeList = sel.select(treeList);
        if (++n == 10)
        {
            treeGroups = sel.select(treeGroups, false);
            n = 0;
        }
        asignTrees(treeList, treeGroups);
    }

    public void evolve(TreeOperator op)
    {
        op.operate(treeList);
        if (n==0)
        {
            tgo.operate(treeGroups);
            asignTrees(treeList, treeGroups);
        }
    }

    private void asignTrees(List<BufferedTree> trees, List<TreeGroup> groups)
    {
        for (BufferedTree t : trees)
        {
            t.nGroups = 0;
        }
        for (TreeGroup g : groups)
        {
            for (int i = 0; i < g.nTrees(); i++)
            {
                if (g.getTree(i) != null)
                    g.getTree(i).nGroups++;
            }
        }
        int treePointer = 0;
        //first we use the new trees:
        List<BufferedTree> newTrees = new ArrayList<BufferedTree>();
        for (BufferedTree t : trees)
            if (t.nGroups==0)
                newTrees.add(t);
        int nNewTrees = newTrees.size();
        BufferedTree[] sortedTrees;
        BufferedTree[] allSortedTrees;
        boolean first = true;
        if (nNewTrees != 0)
        {
            sortedTrees = newTrees.toArray(new BufferedTree[nNewTrees]);
            Arrays.sort(sortedTrees, new IndFitnessComparator());
        }
        else
        {
            first = false;
            allSortedTrees = treeList.toArray(new BufferedTree[config.nTrees]);
            Arrays.sort(allSortedTrees, new IndFitnessComparator());
            nNewTrees = config.nTrees;
            sortedTrees = allSortedTrees;
        }


        for (TreeGroup g : groups)
        {
            for (int i = 0; i < g.nTrees(); i++)
            {
                //if a tree didn't get selected, its isOnPop will be false
                if (g.getTree(i) == null || !g.getTree(i).isOnPop())
                {
                    g.setTree(i, sortedTrees[treePointer]);
                    sortedTrees[treePointer].nGroups++;
                    if (++treePointer == nNewTrees)
                    {
                        if (first)
                        {
                            //then we use all the trees:
                            first = false;
                            allSortedTrees = treeList.toArray(new BufferedTree[config.nTrees]);
                            Arrays.sort(allSortedTrees, new IndFitnessComparator());
                            nNewTrees = config.nTrees;
                            sortedTrees = allSortedTrees;
                        }
                        treePointer = 0;
                    }
                }
            }
        }
    }

}
