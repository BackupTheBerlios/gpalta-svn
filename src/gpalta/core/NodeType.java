/*
 * NodeType.java
 *
 * Created on 13 de octubre de 2005, 10:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gpalta.core;

import java.util.*;
import gpalta.nodes.*;

/**
 *
 * @author neven
 */
public class NodeType 
{
    
    public List<Node> all;
    public List<Node> terminals;
    public List<Node> functions;
    
    /** Creates a new instance of NodeType */
    public NodeType() 
    {
        all = new ArrayList<Node>();
        terminals = new ArrayList<Node>();
        functions = new ArrayList<Node>();
    }
    
}
