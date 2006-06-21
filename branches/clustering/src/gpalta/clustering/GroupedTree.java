/*
 * GroupedTree.java
 *
 * Created on 7 de junio de 2006, 06:05 PM
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

    public void resetGroups()
    {
        groups.clear();
    }

    public Individual deepClone()
    {
        GroupedTree t = (GroupedTree) super.deepClone();
        t.out = (Output)out.clone();
        return t;
    }
    
}
