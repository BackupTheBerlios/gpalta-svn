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
    
    public NodeBuilder nodeBuilderGrow;
    public NodeBuilder nodeBuilderFull;
    public int[] nTreesEachDepth;
    
    /** Creates a new instance of TreeBuilder */
    public TreeBuilder() 
    {
        nodeBuilderGrow = new NodeBuilderGrow();
        nodeBuilderFull = new NodeBuilderFull();
        nTreesEachDepth = new int[Config.maxDepth - Config.initialMinDepth + 1];
        /* nTreesEachDepth will contain the number of trees created for each
         * depth from initialMinDepth to maxDepth
         * ie:
         * nTreesEachDepth[0] = number of trees crated with max depth initialMinDepth
         * nTreesEachDepth[1] = number of trees crated with max depth initialMinDepth + 1
         * and so on
         * This is done from greater to lower depth in order to favor larger trees.
         */
        int depth = Config.maxDepth;
        for (int i=0; i<Config.populationSize; i++)
        {
            if (depth == Config.initialMinDepth - 1)
            {
                depth = Config.maxDepth;
            }
            nTreesEachDepth[depth - Config.initialMinDepth]++;
            depth--;
        }
    }
    
    public void build (List<Tree> population)
    {
        int depth = Config.initialMinDepth;
        Tree tree;
        int treesDoneThisDepth = 0;
        for (int i=0; i<Config.populationSize; i++)
        {
            tree = new Tree(Types.treeRoot);
            build(tree, depth);
            population.add(tree);
            
            treesDoneThisDepth++;
            if (treesDoneThisDepth == nTreesEachDepth[depth - Config.initialMinDepth])
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
        tree.kids[0] = Types.newRandomNode(tree.typeOfKids(0).all, 0);
        tree.kids[0].init();
        
        if (Common.globalRandom.nextDouble() <= Config.probGrowBuild)
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
