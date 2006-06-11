/*
 * TreeGroup.java
 *
 * Created on 6 de junio de 2006, 04:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gpalta.clustering;
import gpalta.core.*;
import java.util.*;

/**
 *
 * @author neven
 */
public class TreeGroup extends Individual
{
    private GroupedTree[] trees;
    
    /** Creates a new instance of TreeGroup */
    public TreeGroup(int nTrees)
    {
        trees = new GroupedTree[nTrees];
    }

    public int getSize()
    {
        return trees.length;
    }

    public Individual deepClone()
    {
        TreeGroup out = new TreeGroup(trees.length);
        for (int i=0; i<trees.length; i++)
            out.set(i, (GroupedTree)get(i).deepClone());
        return out;
    }
    
    public void set(int pos, GroupedTree t)
    {
        trees[pos] = t;
    }
    
    public GroupedTree get(int pos)
    {
        return trees[pos];
    }
    
}
