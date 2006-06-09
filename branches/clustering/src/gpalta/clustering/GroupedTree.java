/*
 * GroupedTree.java
 *
 * Created on 7 de junio de 2006, 06:05 PM
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
public class GroupedTree extends Tree
{
    public Output out;
    public List<TreeGroup> groups;
    
    /** Creates a new instance of GroupedTree */
    public GroupedTree(NodeSet type)
    {
        super(type);
        groups = new ArrayList<TreeGroup>();
    }
    
}
