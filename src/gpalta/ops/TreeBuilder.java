/*
 * TreeBuilder.java
 *
 * Created on 18 de mayo de 2005, 08:22 PM
 *
 * Copyright (C) 2005  Neven Boric <nboric@gmail.com>
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
 * 
 * @author neven
 */
public class TreeBuilder
{
    
    private NodeBuilder nodeBuilderGrow;
    private NodeBuilder nodeBuilderFull;
    private int[] nTreesEachDepth;
    private Config config;
    private NodeTypesConfig types;
    
    /** Creates a new instance of TreeBuilder */
    public TreeBuilder(Config config, NodeTypesConfig types) 
    {
        this.config = config;
        this.types = types;
        
        nodeBuilderGrow = new NodeBuilderGrow(types);
        nodeBuilderFull = new NodeBuilderFull(types);
        nTreesEachDepth = new int[config.maxDepth - config.initialMinDepth + 1];
        /* nTreesEachDepth will contain the number of trees created for each
         * depth from initialMinDepth to maxDepth
         * ie:
         * nTreesEachDepth[0] = number of trees crated with max depth initialMinDepth
         * nTreesEachDepth[1] = number of trees crated with max depth initialMinDepth + 1
         * and so on
         * This is done from greater to lower depth in order to favor larger trees.
         */
        int depth = config.maxDepth;
        for (int i=0; i<config.populationSize; i++)
        {
            if (depth == config.initialMinDepth - 1)
            {
                depth = config.maxDepth;
            }
            nTreesEachDepth[depth - config.initialMinDepth]++;
            depth--;
        }
    }
    
    public void build (List<Tree> population)
    {
        int depth = config.initialMinDepth;
        Tree tree;
        int treesDoneThisDepth = 0;
        for (int i=0; i<config.populationSize; i++)
        {
            tree = new Tree(types.treeRoot);
            build(tree, depth);
            population.add(tree);
            
            treesDoneThisDepth++;
            if (treesDoneThisDepth == nTreesEachDepth[depth - config.initialMinDepth])
            {
                depth++;
                treesDoneThisDepth = 0;
            }
        }
    }
    
    private void build (Tree tree, int maxDepth)
    {
        tree.currentDepth = -1;
        tree.kids = new Node[1];
        tree.kids[0] = types.newRandomNode(tree.typeOfKids(null, 0).all, 0);
        
        if (Common.globalRandom.nextDouble() <= config.probGrowBuild)
        {
            nodeBuilderGrow.build(tree.kids[0], maxDepth);
        }
        else
        {
            nodeBuilderFull.build(tree.kids[0], maxDepth);
        }
        
        tree.kids[0].parent = tree;
        tree.nSubNodes = 1 + tree.kids[0].nSubNodes;
        tree.maxDepthFromHere = 1 + tree.kids[0].maxDepthFromHere;
    }
    
}
