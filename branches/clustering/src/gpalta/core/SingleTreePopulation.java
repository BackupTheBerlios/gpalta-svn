/*
 * SingleTreePopulation.java
 *
 * Created on 31 de mayo de 2006, 05:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gpalta.core;

import gpalta.nodes.Tree;
import gpalta.ops.TreeBuilder;
import gpalta.ops.TreeSelector;
import java.util.List;

/**
 *
 * @author neven
 */
public class SingleTreePopulation implements Population
{
    private Tree[] trees;
    private Config config;

    public void eval(Fitness f)
    {
        for (int i=0; i<trees.length; i++)
        {
            
        }
    }

    public List<Double> getOutput(int which)
    {
    }

    public Individual get(int which)
    {
    }

    public void add(Individual ind)
    {
    }

    public void doSelection(TreeSelector sel)
    {
    }

    public void init(Config config, TreeBuilder builder)
    {
        this.config = config;
        trees = new Tree[config.populationSize];
    }
    
}
