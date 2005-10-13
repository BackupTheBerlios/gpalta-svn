/*
 * NodeBuilderGrow.java
 *
 * Created on 18 de mayo de 2005, 07:26 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package gpalta.ops;
import java.util.*;
import gpalta.nodes.*;
import gpalta.core.*;

/**
 * Implements the 'GROW' build method
 * @author neven
 */
public class NodeBuilderGrow extends NodeBuilder
{
    
    public void build (Node node, int maxDepth)
    {
        build (node, -1, maxDepth);
    }
    
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
            //If maxDepth = 1, we need terminals as kids:
            if (maxDepth == 1)
            {
                node.kids[i] = Types.newRandomNode(node.typeOfTerminalKids(), currentGlobalDepth + 1);
                node.nSubNodes += 1;
            }
            else
            {
                node.kids[i] = Types.newRandomNode(node.typeOfKids(), currentGlobalDepth + 1);
                build(node.kids[i], -1, maxDepth-1);
                
                /* If we are building only one child, these will be wrong for the 
                 * first parent, but will be corrected by updateParents()
                 */
                node.nSubNodes += 1 + node.kids[i].nSubNodes;
                maxDepthOfKids = Math.max(maxDepthOfKids, node.kids[i].maxDepthFromHere);
            }
            node.kids[i].parent = node;
            node.kids[i].whichKidOfParent = i;
        }
        if (node.nKids() > 0)
            node.maxDepthFromHere = 1 + maxDepthOfKids;
    }
    
}
