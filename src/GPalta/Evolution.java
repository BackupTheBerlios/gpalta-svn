/*
 * Evolution.java
 *
 * Created on 18 de mayo de 2005, 07:31 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package GPalta;
import nodes.*;
import ops.*;
import java.util.*;
import java.io.*;


/**
 * Holds the population and performs evolution
 * @author neven
 */
public class Evolution extends Thread
{
    
    public TreeBuilder treeBuilder;
    public List<Tree> population;
    public TreeOperator treeOp;
    public TreeSelector treeSelector;
    public Fitness fitness;
    public int generation;

    public EvolutionStats evoStats;
    
    public Evolution()
    {
        
        RealDataHolder.init();
        LogicDataHolder.init();
        Types.define();
        
        population = new ArrayList<Tree>();
        
        treeBuilder = new TreeBuilder();
        treeBuilder.build(population);
        
        treeOp = new TreeOperator();
        treeSelector = new TreeSelector();
        fitness = new Fitness();

        evoStats = new EvolutionStats();
        evoStats.bestSoFar = population.get(0);
        
        generation = 0;
        
    }
    
    /**
     * Evaluate the current population.
     */
    public void eval()
    {
        Tree bestThisGen = population.get(0);
        double avgFit = 0;
        double avgNodes = 0;
        
        for (Tree t : population)
        {
            fitness.calculate(t);
            avgFit += t.fitness;
            avgNodes += t.nSubNodes;
            if (t.fitness > bestThisGen.fitness)
            {
                bestThisGen = t;
            }
        }
        
        avgFit /= Config.populationSize;
        avgNodes /= Config.populationSize;

        //This should be done by the controlling class:
        /*System.out.format("Gen %d: best fit %.3f", generation, bestThisGen.fitness);
        System.out.format(", average %.3f, best so far %.3f\n", avgFit, evoStats.bestSoFar.fitness);*/
        
        evoStats.bestTreeChanged = false;
        if (bestThisGen.fitness > evoStats.bestSoFar.fitness)
        {
            evoStats.bestSoFar = (Tree)bestThisGen.deepClone(-1);
            evoStats.bestTreeChanged = true;
        }
        
        /* This is done here to ensure that evoStats fields are not modified until
         * the next gen is fully evaluated. This is to give time for other methods to
         * read the fields.
         * TODO: This is clearly not the best solution
         */
        evoStats.avgFit = avgFit;
        evoStats.avgNodes = avgNodes;
        evoStats.generation = generation;
        evoStats.bestFitThisGen = bestThisGen.fitness;
    }
    
    public void evolve()
    {
        List<Tree> nextPop = treeSelector.select(population);
        treeOp.operate(nextPop);
        population = nextPop;
        generation ++;
    }
    
    
    
    /* TODO: We should do something to check that the saved info is correct.
     * Maybe more things should be saved. For instance, if the file was saved
     * with a grater maxDepth than it is now, there would be nodes with larger 
     * depth than current maxDepth, and an attempt to mutate those nodes would
     * result in an error.
     */
    //throw exceptions so that controlling classes do something if errors exist
    public void save(String filename) throws IOException
    {
        Logger.log("******************************************");
        Logger.log("Saving in file " + filename);
        Logger.log("Best tree so far:");
        Logger.log("" + evoStats.bestSoFar);
        Logger.log("with fitness: " + evoStats.bestSoFar.fitness);
        
        
        FileOutputStream fos = new FileOutputStream(filename);
        ObjectOutputStream out = new ObjectOutputStream(fos);

        out.writeInt(generation);
        out.writeObject(evoStats.bestSoFar);
        out.writeObject(population);

        out.close();
        
        Logger.log("Done");
        Logger.log("******************************************");

    }
    //throw exceptions so that controlling classes do something if errors exist
    public void read(String filename) throws IOException, ClassNotFoundException
    {
        FileInputStream fis = new FileInputStream(filename);
        ObjectInputStream in = new ObjectInputStream(fis);
        
        generation = in.readInt();
        evoStats.generation = generation;
        evoStats.bestSoFar = (Tree)in.readObject();
        population = (ArrayList<Tree>)in.readObject();

        in.close();

    }
    
}
