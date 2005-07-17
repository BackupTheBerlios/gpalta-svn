/*
 * NodeBuilderFull.java
 *
 * Created on 18 de mayo de 2005, 07:22 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package ops;
import java.util.*;
import nodes.*;
import GPalta.*;

/**
 * Implements the 'FULL' build method
 * @author neven
 */
public class NodeBuilderFull extends NodeBuilder
{
    public void build (Node node, int maxDepth)
    {
        build (node, -1, maxDepth);
    }
    
    
    /**
     * Implements the 'FULL' build method
     * 
     * @param node The {@link Node} to build (i.e. modify its kids)
     * @param whichKid If -1, modify all kids. Else, modify only kid number wichKid
     * @param maxDepth The number of levels this {@link Node} must have (relative to himself)
     */
    public void build (Node node, int whichKid, int maxDepth)
    {
        int currentGlobalDepth = node.currentDepth;
        List<Integer> listOfKids = new ArrayList<Integer>();
        
        //whichKid = -1 means: build all kids
        if (whichKid == -1)
        {
            if (node.nKids() > 0)
                node.kids = new Node[node.nKids()];
            for (int i=0; i<node.nKids(); i++)
            {
                listOfKids.add(i);
            }            
        }
        
        //build only whichKid
        else
        {
            listOfKids.add(whichKid);
        }
        
        
        int maxDepthOfKids = 0;
        for (Integer i : listOfKids)
        {
            List<Node> globalTypeOfKids = node.typeOfKids();

            //If maxDepth = 1, we need terminals as kids:
            if (maxDepth == 1)
            {
                if (globalTypeOfKids.get(0) instanceof RealNode)
                {
                    node.kids[i] = Types.newRandomNode(Types.realTerminal, currentGlobalDepth + 1);
                }
                else if (globalTypeOfKids.get(0) instanceof LogicNode)
                {
                    node.kids[i] = Types.newRandomNode(Types.logicTerminal, currentGlobalDepth + 1);
                }
                node.nSubNodes += 1;
            }
            else
            {
                if (globalTypeOfKids.get(0) instanceof RealNode)
                {
                    node.kids[i] = Types.newRandomNode(Types.realFunction, currentGlobalDepth + 1);
                }
                else if (globalTypeOfKids.get(0) instanceof LogicNode)
                {
                    node.kids[i] = Types.newRandomNode(Types.logicFunction, currentGlobalDepth + 1);
                }                
                build(node.kids[i], -1, maxDepth-1);
                
                /* If we are building only one child, these will be wrong for the 
                 * first parent, but will be corrected by updateParents()
                 */
                node.nSubNodes += 1 + node.kids[i].nSubNodes;
                maxDepthOfKids = Math.max(maxDepthOfKids, node.kids[i].maxDepthFromHere);
            }
            node.kids[i].whichKidOfParent = i;
            node.kids[i].parent = node;
        }
        if (node.nKids() > 0)
            node.maxDepthFromHere = 1 + maxDepthOfKids;
    }
    
}
