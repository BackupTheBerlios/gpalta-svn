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
            getTerminalNodes(l, tree.kids[0]);
        }
        else if (type <= Config.upLimitProbSelectNonTerminal)
        {
            getFunctionNodes(l, tree.kids[0]);
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

        if (type <= Config.upLimitProbSelectTerminal)
        {
            getNodes(l, tree.kids[0], node.parent.typeOfKids().terminals);
        }
        else if (type <= Config.upLimitProbSelectNonTerminal)
        {
            getNodes(l, tree.kids[0], node.parent.typeOfKids().functions);
        }
        else
        {
            getNodes(l, tree.kids[0], node.parent.typeOfKids().all);
        }
        //TODO: what should we do if we don't find any node?
        if (l.size() == 0)
        {
            return null;
        }
        int which = Common.globalRandom.nextInt(l.size());
        return l.get(which);
    }
    
    private void getNodes(List<Node> l, Node node, List<Node> types)
    {
        if (Types.isInList(node, types))
        {
            l.add(node);
        }
        for (int i=0; i<node.nKids(); i++)
        {
            getNodes(l, node.kids[i], types);
        }
    }
    
    /**
     * Picks any node of any kind within the tree. O(logn)
     */
    private Node pickRandomAnyNode(Tree tree)
    {
        int which = Common.globalRandom.nextInt(tree.nSubNodes);
        currentNodeSearched = 0;
        return getNode(tree.kids[0],which);
    }
    
    private Node getNode(Node node, int which)
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
                return getNode(node.kids[i], which);
            }
        }
        //we should never get here:
        Logger.log("Warning in PickNode: Reached dead end");
        return null;
    }
    
    private void getTerminalNodes(List<Node> l, Node node)
    {
        if (node.nKids() == 0)
        {
            l.add(node);
        }
        for (int i=0; i<node.nKids(); i++)
        {
            getTerminalNodes(l, node.kids[i]);
        }
    }
    
    private void getFunctionNodes(List<Node> l, Node node)
    {
        if (node.nKids() > 0)
        {
            l.add(node);
        }
        for (int i=0; i<node.nKids(); i++)
        {
            getFunctionNodes(l, node.kids[i]);
        }
    }
    
}
