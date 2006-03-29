/*
 * NodeType.java
 *
 * Created on 13 de octubre de 2005, 10:26 PM
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

package gpalta.core;

import java.util.*;
import gpalta.nodes.*;

/**
 *
 * @author neven
 */
public class NodeSet 
{
    
    public List<Node> all;
    public List<Node> terminals;
    public List<Node> functions;
    public String name;
    
    /** Creates a new instance of NodeType */
    public NodeSet(String name) 
    {
        this.name = name;
        all = new ArrayList<Node>();
        terminals = new ArrayList<Node>();
        functions = new ArrayList<Node>();
    }
    
}
