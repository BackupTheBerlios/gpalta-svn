/*
 * NodeBuilder.java
 *
 * Created on 18 de mayo de 2005, 07:40 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package ops;
import nodes.*;

/**
 * Abstract Node Builder
 * @author neven
 */
public abstract class NodeBuilder 
{
    
    public abstract void build (Node node, int maxDepth);
    
    public abstract void build (Node node, int whichKid, int maxDepth);
    
}
