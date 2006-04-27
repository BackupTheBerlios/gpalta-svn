/*
 * TreeOperator.java
 *
 * Created on 13 de mayo de 2005, 09:19 PM
 *
 * Copyright (C) 2005, 2006 Neven Boric <nboric@gmail.com>
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

package gpalta.ops;
import gpalta.nodes.*;
import java.util.*;
import gpalta.core.*;

/**
 * Performs genetic operations between trees. Currently crossover, mutation
 * and reproduction are implemented
 * @author neven
 */
public class TreeOperator 
{

    private NodeSelector selector;
    private NodeBuilder nodeBuilder;
    private Config config;
    
    public TreeOperator(Config config, NodeFactory nodeFactory)
    {
        this.config = config;
        selector = new NodeSelector(config, nodeFactory);
        nodeBuilder = new NodeBuilderGrow(nodeFactory);
    }
    
    /**
     * Performs the genetic operations. Probabilities for each op are assigned
     * in the Config object
     *
     * @param population The list of trees that passed the selection process
     */
    public void operate(List<Tree> population)
    {
        int[] perm = Common.randPerm(config.populationSize);
        double op;
        for (int i=0; i< config.populationSize; i++)
        {
            op = Common.globalRandom.nextDouble();
            if (op <= config.upLimitProbCrossOver)
            {
                //Do cross over, except for the last tree:
                if (i != config.populationSize-1)
                {
                    population.get(perm[i]).fitCalculated = false;
                    population.get(perm[i+1]).fitCalculated = false;
                    crossOver(population.get(perm[i]), population.get(perm[++i]));
                }
            }
            else if (op <= config.upLimitProbMutation)
            {
                population.get(perm[i]).fitCalculated = false;
                mutateBuild(population.get(perm[i]));
            }
            else
            {
                //Do nothing (reproduction)
            }
        }
    }
    
    private void mutateBuild(Tree tree)
    {
        Node tmp = selector.pickRandomNode(tree);
        //Choose a random depth between 1 and (config.maxDepth - currentDepth)
        int depthFromHere = 1 + Common.globalRandom.nextInt(config.maxDepth - tmp.currentDepth + 1);
        //System.out.println("Mut: " + tmp.currentDepth + " " + depthFromHere);
        nodeBuilder.build(tmp.parent, tmp.whichKidOfParent, depthFromHere);
        updateParents(tmp);
    }
    
    private void crossOver(Tree tree1, Tree tree2)
    {
        Node node1;
        Node node2;
        
        for (int i=0; i<config.maxCrossoverTries; i++)
        {
            node1 = selector.pickRandomNode(tree1);
            node2 = selector.pickRandomNode(tree2, node1);
            if (node2 != null)
            {
                if (node1.currentDepth + node2.maxDepthFromHere <= config.maxDepth  &&
                    node2.currentDepth + node1.maxDepthFromHere <= config.maxDepth)
                {
                    //System.out.println("CO: " + node1.currentDepth + " " + node2.currentDepth);
                    
                    /* TODO: we shouldn't need to deepClone(), because two trees are
                     * never the same (if a tree is selected more than once, it's deepCloned)
                     * But, we need to update the depth of the swaped nodes and their kids,
                     * so I'm leaving it
                     */
                    
                    Node node1copy = node1.deepClone(node2.currentDepth);
                    Node node2copy = node2.deepClone(node1.currentDepth);
                    
                    node1.parent.kids[node1.whichKidOfParent] = node2copy;
                    node2.parent.kids[node2.whichKidOfParent] = node1copy;
                    
                    node1copy.parent = node2.parent;
                    node2copy.parent = node1.parent;
                    
                    node1copy.whichKidOfParent = node2.whichKidOfParent;
                    node2copy.whichKidOfParent = node1.whichKidOfParent;
                    
                    updateParents(node1copy);
                    updateParents(node2copy);
                    return;
                }
            }
        }
        //if we reach here, neither tree was modified:
        tree1.fitCalculated = tree2.fitCalculated = true;
        //System.out.println("Crossover failed after " + config.maxCrossoverTries + " tries");
    }
    
    /**
     * Recalculate nSubNodes and maxDepthFromHere for all parents up to rootNode
     * This is necessary to keep the assumption that nodes always know both these values
     */
    private void updateParents(Node node)
    {
        while (node.parent != null)
        {
            node = node.parent;
            node.nSubNodes = 0;
            int maxDepth = 0;
            for (int i=0; i<node.nKids(); i++)
            {
                node.nSubNodes += 1 + node.kids[i].nSubNodes;
                maxDepth = Math.max(maxDepth, node.kids[i].maxDepthFromHere);
            }
            node.maxDepthFromHere = maxDepth + 1;
        }
    }
    
}
