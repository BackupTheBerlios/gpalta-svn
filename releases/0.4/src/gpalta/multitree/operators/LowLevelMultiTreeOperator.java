/*
 * LowLevelMultiTreeOperator.java
 *
 * Created on 21-03-2007, 03:45:37 PM
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
import gpalta.core.*;
import gpalta.multitree.MultiTreeIndividual;
import gpalta.multitree.MultiOutput;
import gpalta.clustering.InformationTheory;

import java.util.*;

public abstract class LowLevelMultiTreeOperator
{
    protected TreeOperator treeOp;
    public int timesApplied;
    public int timesFitnessImproved;

    public LowLevelMultiTreeOperator(TreeOperator treeOp)
    {
        this.treeOp = treeOp;
    }

    public abstract boolean operate(MultiTreeIndividual[] individuals, TempVectorFactory tempVectorFactory, ProblemData problemData);
    public abstract int nIndividuals();

    public int selectWorstTree(MultiTreeIndividual ind)
    {
        int min = 0;
        double minFit = ind.getTree(0).readFitness();
        for (int i=1; i< ind.nTrees(); i++)
        {
            if (ind.getTree(i).readFitness() < minFit)
            {
                minFit = ind.getTree(i).readFitness();
                min = i;
            }
        }
        return min;
    }

    public int selectBestTree(MultiTreeIndividual ind)
    {
        int max = 0;
        double maxFit = ind.getTree(0).readFitness();
        for (int i=1; i< ind.nTrees(); i++)
        {
            if (ind.getTree(i).readFitness() > maxFit)
            {
                maxFit = ind.getTree(i).readFitness();
                max = i;
            }
        }
        return max;
    }

    public int selectWorstTreeRoulette(MultiTreeIndividual ind)
    {
        int n = ind.nTrees();
        double[] unFit = new double[n];
        for (int i = 0; i < n; i++)
        {
            unFit[i] = 1/(1+ind.getTree(i).readFitness());
        }
        return selectRoulette(unFit);
    }

    public int selectBestTreeRoulette(MultiTreeIndividual ind)
    {
        int n = ind.nTrees();
        double[] fit = new double[n];
        for (int i = 0; i < n; i++)
        {
            fit[i] = ind.getTree(i).readFitness();
        }
        return selectRoulette(fit);
    }

    public int selectMostSimilarTreeRoulette(Tree tree1, MultiTreeIndividual multiTree2, TempVectorFactory tempVectorFactory, ProblemData problemData)
    {
        SingleOutput out1 = new SingleOutput(problemData.nSamples);
        tree1.evalVect(out1, tempVectorFactory, problemData);
        Common.sigmoid(out1.x);

        int dim2 = multiTree2.nTrees();
        MultiOutput out2 = new MultiOutput(dim2, problemData.nSamples);
        multiTree2.evalVect(out2, tempVectorFactory, problemData);
        double[] invDiff = new double[dim2];
        for (int i=0; i<dim2; i++)
        {
            invDiff[i] = 1/(1+diffBetweenOutputs(out1.x, out2.getArray(i)));
        }
        return selectRoulette(invDiff);
    }

    public int selectRoulette(double[] scores)
    {
        int n = scores.length;
        List<IndexedObject<Double>> indexedScores = new ArrayList<IndexedObject<Double>>(n);
        for (int i=0; i<n; i++)
        {
            indexedScores.add(new IndexedObject<Double>(i, scores[i]));
        }
        Collections.sort(indexedScores, new Comparator<IndexedObject<Double>>()
        {
            public int compare(IndexedObject<Double> o1, IndexedObject<Double> o2)
            {
                return o1.object.compareTo(o2.object);
            }
        });
        double[] acScore = new double[n];
        double sum = 0;
        for (int i = 0; i < n; i++)
        {
            sum += indexedScores.get(i).object;
            acScore[i] = sum;
        }
        for (int i = 0; i < n; i++)
        {
            acScore[i] /= sum;
        }
        double r = Common.globalRandom.nextDouble();
        for (int i = 0; i < n; i++)
        {
            if (r < acScore[i])
            {
                return indexedScores.get(i).index;
            }
        }
        return indexedScores.get(n-1).index;
    }

    public int selectMostSimilarTree(Tree tree1, MultiTreeIndividual multiTree2, TempVectorFactory tempVectorFactory, ProblemData problemData)
    {
        SingleOutput out1 = new SingleOutput(problemData.nSamples);
        tree1.evalVect(out1, tempVectorFactory, problemData);
        Common.sigmoid(out1.x);

        MultiOutput out2 = new MultiOutput(multiTree2.nTrees(), problemData.nSamples);
        multiTree2.evalVect(out2, tempVectorFactory, problemData);
        for (int i=0; i<out2.getDim(); i++)
            Common.sigmoid(out2.getArray(i));

        int min = 0;
        double minDiff = Common.dist2(out1.x, out2.getArray(0));
        for (int i=1; i<out2.getDim(); i++)
        {
            double diff = diffBetweenOutputs(out1.x, out2.getArray(i));
            if (diff < minDiff)
            {
                min = i;
                minDiff = diff;
            }
        }
        return min;
    }

    public int selectMostSimilarTreeMutInf(Tree tree1, MultiTreeIndividual multiTree2, TempVectorFactory tempVectorFactory, ProblemData problemData)
    {
        SingleOutput out1 = new SingleOutput(problemData.nSamples);
        tree1.evalVect(out1, tempVectorFactory, problemData);
        Common.sigmoid(out1.x);

        MultiOutput out2 = new MultiOutput(multiTree2.nTrees(), problemData.nSamples);
        multiTree2.evalVect(out2, tempVectorFactory, problemData);
        for (int i=0; i<out2.getDim(); i++)
            Common.sigmoid(out2.getArray(i));

        double[] px = InformationTheory.px(out1.x, 0, 1, 100);
        int max = 0;
        double[] py = InformationTheory.px(out2.getArray(0), 0, 1, 100);
        double[][] pxy = InformationTheory.pxy(out1.x, out2.getArray(0), 0, 1, 100);
        double maxMutInf = InformationTheory.mutualInformation(px, py, pxy);
        for (int i=1; i<out2.getDim(); i++)
        {
            py = InformationTheory.px(out2.getArray(i), 0, 1, 100);
            pxy = InformationTheory.pxy(out1.x, out2.getArray(i), 0, 1, 100);
            double mutInf = InformationTheory.mutualInformation(px, py, pxy);
            if (mutInf > maxMutInf)
            {
                max = i;
                maxMutInf = mutInf;
            }
        }
        return max;
    }

    public double diffBetweenOutputs(double[] x1, double[] x2)
    {
        return Common.dist2(x1, x2);
    }

}

class IndexedObject<T>
{
    public final T object;
    public final int index;
    public IndexedObject(int index, T object)
    {
        this.object = object;
        this.index = index;
    }
}

class IndexedIndFitnessComparator implements Comparator<IndexedObject<? extends Individual>>
{
    public int compare(IndexedObject<? extends Individual> o1, IndexedObject<? extends Individual> o2)
    {
        return new IndFitnessComparator().compare(o1.object, o2.object);
    }
}

