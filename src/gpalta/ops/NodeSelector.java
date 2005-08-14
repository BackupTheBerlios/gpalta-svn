/*
 * NodeSelector.java
 *
 * Created on 12 de mayo de 2005, 01:13 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package gpalta.ops;

import java.util.*;
import gpalta.nodes.*;
import gpalta.core.*;

public class NodeSelector {
    
    private int currentNodeSearched;
   
    public Node pickRandomNode(Tree tree)
    {
        double type = Common.globalRandom.nextDouble();
        List<Node> l = new ArrayList<Node>();
        if (type <= Config.upLimitProbSelectTerminal)
        {
            getTerminalAnyNodes(l, tree.kids[0]);
        }
        else if (type <= Config.upLimitProbSelectNonTerminal)
        {
            getFunctionAnyNodes(l, tree.kids[0]);
            /* If there aren't function nodes, this is a tree with a terminal at its root
             * (Shoudn't we stop this from happening?)
             */
            if (l.size() == 0)
            {
                return tree.kids[0];
            }
        }
        else if (type <= Config.upLimitProbSelectRoot)
        {
            return tree.kids[0];
        }
        else
        {
            return pickRandomAnyNode(tree);
        }        
        int which = Common.globalRandom.nextInt(l.size());
        return l.get(which);
    }
    
    /**
     * Picks any node of the same type as node within the tree. It only checks if the node is real or logic. O(n)
     *
     * @param node The 'sample' Node.
     */
    public Node pickRandomNode(Tree tree, Node node)
    {
        double type = Common.globalRandom.nextDouble();
        List<Node> l = new ArrayList<Node>();
        if (node instanceof RealNode)
        {
            if (type <= Config.upLimitProbSelectTerminal)
            {
                getRealTerminalNodes(l, tree.kids[0]);
            }
            else if (type <= Config.upLimitProbSelectNonTerminal)
            {
                getRealFunctionNodes(l, tree.kids[0]);
            }
            else
            {
                getRealAnyNodes(l, tree.kids[0]);
            }
        }
        else if (node instanceof LogicNode)
        {
            if (type <= Config.upLimitProbSelectTerminal)
            {
                getLogicTerminalNodes(l, tree.kids[0]);
            }
            else if (type <= Config.upLimitProbSelectNonTerminal)
            {
                getLogicFunctionNodes(l, tree.kids[0]);
            }
            else if (type <= Config.upLimitProbSelectRoot)
            {
                return tree.kids[0];
            }
            else
            {
                getLogicAnyNodes(l, tree.kids[0]);
            }
        }
        else
        {
            Logger.log("Warning in pickRandomNode: Unrecognized Node type");
            return null;
        }
        //TODO: what should we do if we don't find any node?
        if (l.size() == 0)
        {
            return null;
        }
        int which = Common.globalRandom.nextInt(l.size());
        return l.get(which);
    }
    
    /**
     * Picks any node of any kind within the tree. O(logn)
     */
    private Node pickRandomAnyNode(Tree tree)
    {
        int which = Common.globalRandom.nextInt(tree.nSubNodes);
        currentNodeSearched = 0;
        return pickNode(tree.kids[0],which);
    }
    
    private Node pickNode(Node node, int which)
    {
        if (currentNodeSearched == which)
        {
            return node;
        }
        for (int i=0; i< node.nKids(); i++)
        {
            if (which > currentNodeSearched + 1 + node.kids[i].nSubNodes)
            {
                currentNodeSearched += 1 + node.kids[i].nSubNodes;
            }
            else
            {
                currentNodeSearched++;
                return pickNode(node.kids[i], which);
            }
        }
        //we should never get here:
        Logger.log("Warning in PickNode: Reached dead end");
        return null;
    }
    
    /* Better implemented through several methods to avoid checking each time
     * what kind of node we're looking for
     */
    private void getRealAnyNodes(List<Node> l, Node node)
    {
        if (node instanceof RealNode)
        {
            l.add(node);
        }
        for (int i=0; i<node.nKids(); i++)
        {
            getRealAnyNodes(l, node.kids[i]);
        }
    }
    
    private void getLogicAnyNodes(List<Node> l, Node node)
    {
        if (node instanceof LogicNode)
        {
            l.add(node);
        }
        for (int i=0; i<node.nKids(); i++)
        {
            getLogicAnyNodes(l, node.kids[i]);
        }
    }
    
    private void getTerminalAnyNodes(List<Node> l, Node node)
    {
        if (node.nKids() == 0)
        {
            l.add(node);
        }
        for (int i=0; i<node.nKids(); i++)
        {
            getTerminalAnyNodes(l, node.kids[i]);
        }
    }
    
    private void getFunctionAnyNodes(List<Node> l, Node node)
    {
        if (node.nKids() > 0)
        {
            l.add(node);
        }
        for (int i=0; i<node.nKids(); i++)
        {
            getFunctionAnyNodes(l, node.kids[i]);
        }
    }
    
    private void getRealTerminalNodes(List<Node> l, Node node)
    {
        if (node instanceof RealNode && node.nKids()==0)
        {
            l.add(node);
        }
        for (int i=0; i<node.nKids(); i++)
        {
            getRealTerminalNodes(l, node.kids[i]);
        }
    }
    
    private void getRealFunctionNodes(List<Node> l, Node node)
    {
        if (node instanceof RealNode && node.nKids()>0)
        {
            l.add(node);
        }
        for (int i=0; i<node.nKids(); i++)
        {
            getRealFunctionNodes(l, node.kids[i]);
        }
    }
    
    private void getLogicTerminalNodes(List<Node> l, Node node)
    {
        if (node instanceof LogicNode && node.nKids()==0)
        {
            l.add(node);
        }
        for (int i=0; i<node.nKids(); i++)
        {
            getLogicTerminalNodes(l, node.kids[i]);
        }
    }
    
    private void getLogicFunctionNodes(List<Node> l, Node node)
    {
        if (node instanceof LogicNode && node.nKids()>0)
        {
            l.add(node);
        }
        for (int i=0; i<node.nKids(); i++)
        {
            getLogicFunctionNodes(l, node.kids[i]);
        }
    }
        
}
