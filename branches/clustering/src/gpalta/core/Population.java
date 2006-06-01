/*
 * Population.java
 *
 * Created on 31 de mayo de 2006, 03:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gpalta.core;

import gpalta.ops.TreeBuilder;
import gpalta.ops.TreeSelector;
import java.util.*;

/**
 *
 * @author neven
 */
public interface Population
{
    
    public void eval(Fitness f);
    
    public List<Double> getOutput(int which);
    
    public Individual get(int which);
    
    public void add(Individual ind);
    
    public void init(Config config, TreeBuilder builder);
    
    public void doSelection(TreeSelector sel);
    
}
