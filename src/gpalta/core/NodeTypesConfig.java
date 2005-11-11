/*
 * Types.java
 *
 * Created on 11 de mayo de 2005, 11:25 PM
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
 * Holds information for the {@link Node}s available and provides methods for
 * random {@link Node} creation (modify this class to alter the functions/terminals set)
 *
 * @author neven
 */
public class NodeTypesConfig
{
    
    public NodeType real;
    public NodeType logic;
    
    public NodeType treeRoot;
    
    private Config config;
    
    /**
     * Define the lists of possible Nodes
     *
     * @param evo An Evolution with its Real and Logic DataHolders already
     * initialized, to determine how many variable NSodes to add to the lists
     */
    public NodeTypesConfig(Config config, DataHolder data, PreviousOutputHolder prev)
    {
        this.config = config;
        
        real = new NodeType();
        
        logic = new NodeType();

        treeRoot = new NodeType();
        
        real.functions.add(new Plus());
        real.functions.add(new Minus());
        real.functions.add(new Times());
        real.functions.add(new Divide());

        real.terminals.add(new RealConstant());
        
        for (int i=0; i<data.nVars; i++)
        {
            real.terminals.add(new RealVar(i+1));
        } 
        
        if (config.usePreviousOutputAsReal)
        {
            for (int i=0; i<prev.nDelays; i++)
            {
                real.terminals.add(new PreviousOutput(i+1));
            }
        }

        real.all.addAll(real.functions);
        real.all.addAll(real.terminals);
        

        logic.functions.add(new And());
        logic.functions.add(new Or());
        logic.functions.add(new GreaterThan());
        logic.functions.add(new LessThan());

        /* TODO: Important: this could add many terminals if nDelays is too large
         * Maybe we could add a single PreviousOutput and create an init() method
         * that randomly defines every copy's delay
         */
        if (!config.usePreviousOutputAsReal)
        {
            for (int i=0; i<prev.nDelays; i++)
            {
                logic.terminals.add(new PreviousOutput(i+1));
            }
        }
        
        //If there aren't any logic terminals, add logic constants for closure:
        if (config.usePreviousOutputAsReal || prev.nDelays == 0)
        {
            logic.terminals.add(new LogicConstant());
        }

        logic.all.addAll(logic.functions);
        logic.all.addAll(logic.terminals);
        
        if (config.problemType.equals("classifier"))
        {
            treeRoot = logic;
        }
        else
        {
            treeRoot = real;
        }
        
    }
    
    /**
     * Obtain a new Node randomly chosen from the given list.
     * The Node is cloned and initialized, so it can be used separatedly
     *
     * @param l The list of Nodes from which to choose
     * @param currentGlobalDepth The depth of the requested Node in the Tree
     */
    public Node newRandomNode(List<Node> l, int currentGlobalDepth)
    {
        int which = Common.globalRandom.nextInt(l.size());
        Node outNode = null;
        try
        {
            outNode = (Node)l.get(which).clone();
        }
        /* This should never happen, as nodes do support cloning, so it's ok
         * to catch this exception here
         */
        catch (CloneNotSupportedException e)
        {
            Logger.log(e);
        }
        outNode.init(config);
        outNode.currentDepth = currentGlobalDepth;     
        return outNode;
    }
    
    public static boolean isInList(Node node, List<Node> l)
    {
        for (Node n : l)
        {
            if (n.getClass() == node.getClass())
                return true;
        }
        return false;
    }
}
