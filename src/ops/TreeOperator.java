/*
 * TreeOperator.java
 *
 * Created on 13 de mayo de 2005, 09:19 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package ops;
import nodes.*;
import java.util.*;
import GPalta.*;

/**
 *
 * @author neven
 */
public class TreeOperator 
{

    NodeSelector selector;
    NodeBuilder nodeBuilder;
    
    public TreeOperator()
    {
        selector = new NodeSelector();
        nodeBuilder = new NodeBuilderGrow();
    }
    
    public void operate(List<Tree> population)
    {
        int[] perm = Common.randPerm(Config.populationSize);
        double op;
        for (int i=0; i< Config.populationSize; i+=2)
        {
            op = Common.globalRandom.nextDouble();
            if (op <= Config.upLimitProbCrossOver)
            {
                crossOver(population.get(perm[i]), population.get(perm[i+1]));
            }
            else if (op <= Config.upLimitProbMutation)
            {
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
        //Choose a random depth between 1 and (Config.maxDepth - currentDepth)
        int depthFromHere = 1 + Common.globalRandom.nextInt(Config.maxDepth - tmp.currentDepth + 1);
        //System.out.println("Mut: " + tmp.currentDepth + " " + depthFromHere);
        nodeBuilder.build(tmp.parent, tmp.whichKidOfParent, depthFromHere);
        updateParents(tmp);
    }
    
    private void crossOver(Tree tree1, Tree tree2)
    {
        Node node1;
        Node node2;
        
        for (int i=0; i<Config.maxCrossoverTries; i++)
        {
            node1 = selector.pickRandomNode(tree1);
            node2 = selector.pickRandomNode(tree2, node1);
            if (node2 != null)
            {
                if (node1.currentDepth + node2.maxDepthFromHere <= Config.maxDepth  &&
                    node2.currentDepth + node1.maxDepthFromHere <= Config.maxDepth)
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
        //System.out.println("Crossover failed after " + Config.maxCrossoverTries + " tries");
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
