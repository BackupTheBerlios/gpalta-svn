/*
 * NodeParent.java
 *
 * Created on 7 de junio de 2006, 04:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gpalta.nodes;

import gpalta.core.NodeSet;

/**
 *
 * @author neven
 */
public interface NodeParent
{
    public NodeParent getParent();
    public void setParent(NodeParent n);
    public int getWhichKidOfParent();
    public void setWhichKidOfParent(int whichKidOfParent);
    public NodeSet typeOfKids(int i);
    public int getNSubNodes();
    public void setNSubNodes(int nSubNodes);
    public Node[] getKids();
    public void setKids(Node[] kids);
    public int getMaxDepthFromHere();
    public void setMaxDepthFromHere(int maxDepthFromHere);
    public int nKids();
    public int getCurrentDepth();
    public void setCurrentDepth(int currentDepth);
}
