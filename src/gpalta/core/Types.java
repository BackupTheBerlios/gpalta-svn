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
    
    /** List of possible Real Nodes, including functions and terminals */
    public static List<Node> realAny;
    /** List of possible Real function Nodes */
    public static List<Node> realFunction;
    /** List of possible Real terminal Nodes */
    public static List<Node> realTerminal;

    /** List of possible Logic Nodes, including functions and terminals */
    public static List<Node> logicAny;
    /** List of possible Logic function Nodes */
    public static List<Node> logicFunction;
    /** List of possible Logic terminal Nodes */
    public static List<Node> logicTerminal;
    
    /** List of possible Nodes for the root of a Tree */
    public static List<Node> treeRoot;
    
    /**
     * Define the lists of possible Nodes
     *
     * @param evo An Evolution with its Real and Logic DataHolders already
     * initialized, to determine how many variable NSodes to add to the lists
     */
    public static void define(Evolution evo)
    {
        //Reinitialize Lists every time a new Evolution is created
        realAny = new ArrayList<Node>();
        realFunction = new ArrayList<Node>();
        realTerminal = new ArrayList<Node>();

        logicAny = new ArrayList<Node>();
        logicFunction = new ArrayList<Node>();
        logicTerminal = new ArrayList<Node>();
    
        treeRoot = new ArrayList<Node>();
        
        realFunction.add(new Plus());
        realFunction.add(new Minus());
        realFunction.add(new Times());

        realTerminal.add(new RealConstant());
        
        for (int i=0; i<evo.realDataHolder.nVars; i++)
        {
            realTerminal.add(new RealVar(i+1));
        } 
        
        if (Config.usePreviousOutputAsReal)
        {
            for (int i=0; i<evo.logicDataHolder.nDelays; i++)
            {
                realTerminal.add(new PreviousOutput(i+1));
            }
        }

        realAny.addAll(realFunction);
        realAny.addAll(realTerminal);
        

        logicFunction.add(new And());
        logicFunction.add(new Or());
        logicFunction.add(new GreaterThan());
        logicFunction.add(new LessThan());

        //logicTerminal.add(new LogicConstant());

        /* TODO: Important: this could add many terminals if nDelays is too large
         * Maybe we could add a single PreviousOutput and create an init() method
         * that randomly defines every copy's delay
         */
        if (!Config.usePreviousOutputAsReal)
        {
            for (int i=0; i<evo.logicDataHolder.nDelays; i++)
            {
                logicTerminal.add(new PreviousOutput(i+1));
            }
        }
        
        //If there aren't any logic terminals, add logic constants for closure:
        if (Config.usePreviousOutputAsReal || evo.logicDataHolder.nDelays == 0)
        {
            logicTerminal.add(new LogicConstant());
        }

        logicAny.addAll(logicFunction);
        logicAny.addAll(logicTerminal);
        
        treeRoot = logicAny;
        
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
