/*
 * Types.java
 *
 * Created on 11 de mayo de 2005, 11:25 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
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
public abstract class Types
{
    
    public static NodeType real;
    public static NodeType logic;
    
    public static NodeType treeRoot;
    
    /**
     * Define the lists of possible Nodes
     *
     * @param evo An Evolution with its Real and Logic DataHolders already
     * initialized, to determine how many variable NSodes to add to the lists
     */
    public static void define(Evolution evo)
    {
        //Reinitialize Lists every time a new Evolution is created
        real = new NodeType();
        
        logic = new NodeType();

        treeRoot = new NodeType();
        
        real.functions.add(new Plus());
        real.functions.add(new Minus());
        real.functions.add(new Times());

        real.terminals.add(new RealConstant());
        
        for (int i=0; i<evo.realDataHolder.nVars; i++)
        {
            real.terminals.add(new RealVar(i+1));
        } 
        
        if (Config.usePreviousOutputAsReal)
        {
            for (int i=0; i<evo.logicDataHolder.nDelays; i++)
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
        if (!Config.usePreviousOutputAsReal)
        {
            for (int i=0; i<evo.logicDataHolder.nDelays; i++)
            {
                logic.terminals.add(new PreviousOutput(i+1));
            }
        }
        
        //If there aren't any logic terminals, add logic constants for closure:
        if (Config.usePreviousOutputAsReal || evo.logicDataHolder.nDelays == 0)
        {
            logic.terminals.add(new LogicConstant());
        }

        logic.all.addAll(logic.functions);
        logic.all.addAll(logic.terminals);
        
        treeRoot = real;
        
    }
    
    /**
     * Obtain a new Node randomly chosen from the given list.
     * The Node is cloned and initialized, so it can be used separatedly
     *
     * @param l The list of Nodes from which to choose
     * @param currentGlobalDepth The depth of the requested Node in the Tree
     */
    public static Node newRandomNode(List<Node> l, int currentGlobalDepth)
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
        outNode.init();
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
