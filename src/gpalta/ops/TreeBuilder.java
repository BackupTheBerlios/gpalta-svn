/*
 * TreeBuilder.java
 *
 * Created on 18 de mayo de 2005, 08:22 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
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
