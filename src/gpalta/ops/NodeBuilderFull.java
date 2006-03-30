/*
 * NodeBuilderFull.java
 *
 * Created on 18 de mayo de 2005, 07:22 PM
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
import java.util.*;
import gpalta.nodes.*;
import gpalta.core.*;

/**
 * Implements the 'FULL' build method
 * @author neven
 */
public class NodeBuilderFull extends NodeBuilder
{
    private NodeFactory nodeFactory;
    
    public NodeBuilderFull (NodeFactory nodeFactory)
    {
        this.nodeFactory = nodeFactory;
    }
    
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
            //If maxDepth = 1, we need terminals as kids:
            if (maxDepth == 1)
            {
                node.kids[i] = nodeFactory.newRandomNode(node.typeOfKids(i).getTerminals(), currentGlobalDepth + 1);
                node.nSubNodes += 1;
            }
            else
            {
                node.kids[i] = nodeFactory.newRandomNode(node.typeOfKids(i).getFunctions(), currentGlobalDepth + 1);
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
