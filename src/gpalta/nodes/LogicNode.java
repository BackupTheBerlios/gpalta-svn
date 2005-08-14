/*
 * LogicNode.java
 *
 * Created on 10 de mayo de 2005, 11:13 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package gpalta.nodes;
import java.util.*;
import gpalta.core.*;

/**
 * Generic Node for all type of logic {@link Node}s
 *
 * @author neven
 */
public abstract class LogicNode extends Node
{
        
    public abstract boolean eval(Evolution evo);
    public abstract boolean[] evalVect(Evolution evo);

}
