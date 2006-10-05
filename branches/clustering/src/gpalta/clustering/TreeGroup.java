/*
 * TreeGroup.java
 *
 * Created on 6 de junio de 2006, 04:19 PM
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

import java.util.List;
import java.util.ArrayList;

/**
 * @author neven
 */
public class TreeGroup extends Individual
{
    private List<BufferedTree> treeList;
    private List<Integer> samplesWon;

    /**
     * Creates a new instance of TreeGroup
     */
    public TreeGroup(int nTrees)
    {
        treeList = new ArrayList<BufferedTree>(nTrees);
        for (int i=0; i<nTrees; i++)
            treeList.add(null);
    }

    public int nTrees()
    {
        return treeList.size();
    }

    public int getSize()
    {
        int nodes = 0;
        for (int i = 0; i < nTrees(); i++)
            nodes += treeList.get(i).getSize();
        return nodes;
    }

    public Individual deepClone()
    {
        TreeGroup out = null;
        try
        {
            out = (TreeGroup) clone();
            /* Remember to also clone the tree array and each tree */
            out.treeList = new ArrayList<BufferedTree>(nTrees());
            for (int i = 0; i < nTrees(); i++)
                out.treeList.add(i, (BufferedTree) getTree(i).deepClone());
        }
        catch (CloneNotSupportedException ex)
        {
            Logger.log(ex);
        }
        return out;
    }

    public Individual semiDeepClone()
    {
        TreeGroup out = null;
        try
        {
            out = (TreeGroup) clone();
            /* Remember to also clone the tree array and each tree */
            out.treeList = new ArrayList<BufferedTree>(nTrees());
            for (int i = 0; i < nTrees(); i++)
                out.treeList.add(i, getTree(i));
        }
        catch (CloneNotSupportedException ex)
        {
            Logger.log(ex);
        }
        return out;
    }

    public void setTree(int pos, BufferedTree t)
    {
        treeList.set(pos, t);
    }

    public BufferedTree getTree(int pos)
    {
        return treeList.get(pos);
    }

    public void oneMoreCluster()
    {
        treeList.add(null);
    }

    public void oneLessCluster()
    {
        if (nTrees()!=2)
        {
            double minFit = getTree(0).readFitness();
            int min = 0;
            for (int i=1; i<nTrees(); i++)
            {
                if (getTree(i).readFitness() < minFit)
                {
                    min = i;
                    minFit = getTree(i).readFitness();
                }
            }
            treeList.remove(min);
        }
    }

    public void removeEmptyClusters()
    {
        int nClusters = nTrees();
        if (nClusters < 2)
        {
            int removed = 0;
            for (int i=0; i < nClusters; i++)
            {
                if (samplesWon.get(i-removed) == 0)
                {
                    treeList.remove(i-removed);
                    samplesWon.remove(i-removed);
                }
            }
        }
    }

    public void setSamplesWon(int wCluster, int nSamples)
    {
        samplesWon.set(wCluster, nSamples);
    }
}
