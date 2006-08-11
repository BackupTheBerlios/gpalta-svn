/*
 * BufferedTree.java
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

import java.util.List;
import java.util.ArrayList;

/**
 * @author neven
 */
public class BufferedTree extends Tree
{
    public Output out;
    //public List<TreeGroup> groupList;
    public int nGroups;

    /**
     * Creates a new instance of BufferedTree
     */
    public BufferedTree(NodeSet type)
    {
        super(type);
    }

    public Individual deepClone()
    {
        BufferedTree t = (BufferedTree) super.deepClone();
        t.out = (Output) out.clone();
        t.nGroups = 0;
        return t;
    }

}
