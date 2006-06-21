/*
 * TreeGroup.java
 *
 * Created on 6 de junio de 2006, 04:19 PM
 *
 * Copyright (C) 2006 Neven Boric <nboric@gmail.com>
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

package gpalta.clustering;
import gpalta.core.*;
import java.util.*;

/**
 *
 * @author neven
 */
public class TreeGroup extends Individual implements Cloneable
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
        TreeGroup out = null;
        try 
        {
            out = (TreeGroup)clone();
            /* Remember to also clone the tree array and each tree */
            out.trees = new GroupedTree[trees.length];
            for (int i=0; i<trees.length; i++)
                out.set(i, (GroupedTree)get(i).deepClone());
            return out;
        }
        catch (CloneNotSupportedException ex) 
        {
            Logger.log(ex);
        }
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
