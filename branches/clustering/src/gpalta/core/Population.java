/*
 * Population.java
 *
 * Created on 31 de mayo de 2006, 03:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gpalta.core;

import gpalta.ops.*;
import java.util.*;

/**
 *
 * @author neven
 */
public interface Population
{
    
    public void eval(Fitness f, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev);
    
    /**
     * Get the output directly from the Individual, without further processing
     * @return A totally independent Output object
     */
    public Output getRawOutput(Individual ind, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev);
    
    public Output getProcessedOutput(Individual ind, Fitness f, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev);
    
    public Individual get(int which);
    
    public void init(Config config, DataHolder data, TreeBuilder builder);
    
    public void doSelection(TreeSelector sel);
    
    public void evolve(TreeOperator op);
    
}