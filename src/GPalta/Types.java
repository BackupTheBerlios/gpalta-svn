/*
 * Types.java
 *
 * Created on 11 de mayo de 2005, 11:25 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package GPalta;
import java.util.*;
import nodes.*;

/**
 * Holds information for the {@link Node}s available and provides methods for random {@link Node} creation (modify this class to alter the functions/terminals set)
 *
 * @author neven
 */
public abstract class Types
{
    
    public static List<Node> realAny = new ArrayList<Node>();
    public static List<Node> realFunction = new ArrayList<Node>();
    public static List<Node> realTerminal = new ArrayList<Node>();

    public static List<Node> logicAny = new ArrayList<Node>();
    public static List<Node> logicFunction = new ArrayList<Node>();
    public static List<Node> logicTerminal = new ArrayList<Node>();
    
    public static List<Node> treeRoot = new ArrayList<Node>();
    
    /*public static final int NONE            =-1;
    public static final int REAL_ANY        = 0;
    public static final int REAL_FUNCTION   = 1;
    public static final int REAL_TERMINAL   = 2;
    public static final int LOGIC_ANY       = 3;
    public static final int LOGIC_FUNCTION  = 4;
    public static final int LOGIC_TERMINAL  = 5;
    public static final int FUNCTION_ANY    = 6;
    public static final int TERMINAL_ANY    = 7;*/
    
    public static void define()
    {
        realFunction.add(new Plus());
        realFunction.add(new Minus());
        realFunction.add(new Times());

        realTerminal.add(new RealConstant());
        
        for (int i=0; i<RealDataHolder.nVars; i++)
        {
            realTerminal.add(new RealVar(i+1));
        } 
        
        if (Config.usePreviousOutputAsReal)
        {
            for (int i=0; i<LogicDataHolder.nDelays; i++)
            {
                realTerminal.add(new RealPreviousOutput(i+1));
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
            for (int i=0; i<LogicDataHolder.nDelays; i++)
            {
                logicTerminal.add(new PreviousOutput(i+1));
            }
        }
        
        //If there aren't any logic terminals, add logic constants for closure:
        if (Config.usePreviousOutputAsReal || LogicDataHolder.nDelays == 0)
        {
            logicTerminal.add(new LogicConstant());
        }

        logicAny.addAll(logicFunction);
        logicAny.addAll(logicTerminal);
        
        treeRoot = logicAny;
        
    }
    
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
    
}
