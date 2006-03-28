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
     * @param config The evolution configuration, to determine some options
     * @param data The problem's data, to know how many variables there are
     */
    public NodeTypesConfig(Config config, DataHolder data)
    {
        this.config = config;
        
        real = new NodeType();
        
        logic = new NodeType();

        treeRoot = new NodeType();
        
        real.functions.add(new Plus());
        real.functions.add(new Minus());
        real.functions.add(new Times());
        real.functions.add(new Divide());
        if (config.problemType.equals("clustering"))
            real.functions.add(new IfThenElse());

        real.terminals.add(new RealConstant());
        
        for (int i=0; i<data.nVars; i++)
        {
            real.terminals.add(new RealVar(i+1));
        } 
        
        if (config.usePreviousOutputAsReal)
        {
            for (int i=0; i<config.nPreviousOutput; i++)
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
            for (int i=0; i<config.nPreviousOutput; i++)
            {
                logic.terminals.add(new PreviousOutput(i+1));
            }
        }
        
        //If there aren't any logic terminals, add logic constants for closure:
        if (config.usePreviousOutputAsReal || config.nPreviousOutput == 0)
        {
            logic.terminals.add(new LogicConstant());
        }

        logic.all.addAll(logic.functions);
        logic.all.addAll(logic.terminals);
        
        /* Little hack to avoid so many logic terminals:
         * TODO: fix this (maybe asigning probabilities to each node type)
         */
        logic.all.addAll(logic.functions);
        
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
    
    public Node newNode(String name, int currentGlobalDepth) 
    {
        System.out.println(name);
        Node outNode = null;
        if (name.startsWith("X"))
        {
            outNode = new RealVar(Integer.parseInt(name.substring(1)));
        }
        else if (name.substring(0,1).matches("\\d"))
        {
            outNode = new RealConstant(Double.parseDouble(name));
        }
        else if (name.equals("true"))
        {
            outNode = new LogicConstant(1);
        }
        else if (name.equals("false"))
        {
            outNode = new LogicConstant(0);
        }
        else
        {
            List<Node> all = real.all;
            all.addAll(logic.all);
            for (Node n : all)
            {
                if (name.equals(n.name()))
                {
                    try
                    {
                        outNode = (Node)n.clone();
                    }
                    catch (CloneNotSupportedException e)
                    {
                        Logger.log(e);
                    }
                    break;
                }
            }
        }
        outNode.currentDepth = currentGlobalDepth;
        return outNode;
    }
    
}
