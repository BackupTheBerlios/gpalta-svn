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
public class Evolution
{
    
    public TreeBuilder treeBuilder;
    public List<Tree> population;
    public TreeOperator treeOp;
    public TreeSelector treeSelector;
    public Fitness fitness;
    public RealDataHolder realDataHolder;
    public LogicDataHolder logicDataHolder;
    public int generation;

    public EvolutionStats evoStats;
    
    /**
     * Creates a new instance of Evolution, loading data from file
     * 
     * @param initPop If true, the population is randomly initialized. Else, 
     * nothing is done (population will be later read from a file)
     */
    public Evolution(boolean initPop)
    {
        
        realDataHolder = new RealDataHolder("data.txt");
        logicDataHolder = new LogicDataHolder();
        Types.define(this);
        
        population = new ArrayList<Tree>();
        if (initPop)
        {
            treeBuilder = new TreeBuilder();
            treeBuilder.build(population);
        }
        
        treeOp = new TreeOperator();
        treeSelector = new TreeSelector();
        fitness = new Fitness(this, "class.txt");

        evoStats = new EvolutionStats();
        if (initPop)
        {
            evoStats.bestSoFar = population.get(0);
        }
        
        generation = 0;
        
    }
    
    /**
     * Creates a new instance of Evolution, using the given data, classes and snrs
     * Used for external evaluation of trees, so the population is not
     * initialized, as it will be read later from file.
     */
    public Evolution(double[][] data, boolean[] classes, double[] snrs)
    {
        
        realDataHolder = new RealDataHolder(data);
        logicDataHolder = new LogicDataHolder();
        Types.define(this);
        
        population = new ArrayList<Tree>();
        
        treeOp = new TreeOperator();
        treeSelector = new TreeSelector();
        fitness = new Fitness(this, classes,  snrs);

        evoStats = new EvolutionStats();

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
    
    /**
     * Evolve one generation. Assumes the current population is already evaluated
     * and doesn't evaluate the evolved one
     */
    public void evolve()
    {
        List<Tree> nextPop = treeSelector.select(population);
        treeOp.operate(nextPop);
        population = nextPop;
        generation ++;
    }
    
    
    
    /**
     * Save Evolution to file.
     * TODO: We should do something to check that the saved info is correct.
     * Maybe more things should be saved. For instance, if the file was saved
     * with a grater maxDepth than it is now, there would be nodes with larger 
     * depth than current maxDepth, and an attempt to mutate those nodes would
     * result in an error.
     * 
     * @param fileName The file to write to
     * 
     * @throws IOException if a problem is encountered while writing (controlling
     * classes should do something about it)
     */
    public void save(String fileName) throws IOException
    {
        Logger.log("******************************************");
        Logger.log("Saving in file " + fileName);
        Logger.log("Best tree so far:");
        Logger.log("" + evoStats.bestSoFar);
        Logger.log("with fitness: " + evoStats.bestSoFar.fitness);
        
        
        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream out = new ObjectOutputStream(fos);

        out.writeInt(generation);
        out.writeObject(evoStats.bestSoFar);
        out.writeObject(population);

        out.close();
        
        Logger.log("Done");
        Logger.log("******************************************");

    }
    
    /**
     * Read Evolution from file.
     * TODO: We should do something to check that the saved info is correct.
     * Maybe more things should be saved. For instance, if the file was saved
     * with a grater maxDepth than it is now, there would be nodes with larger 
     * depth than current maxDepth, and an attempt to mutate those nodes would
     * result in an error.
     * 
     * @param fileName The file to be read
     * 
     * @throws IOException if a problem is encountered while reading (controlling
     * classes should do something about it)
     * @throws ClassNotFoundException if class read doesn't match existing classes
     * (probably old data in file)
     */
    public void read(String fileName) throws IOException, ClassNotFoundException
    {
        FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream in = new ObjectInputStream(fis);
        
        generation = in.readInt();
        evoStats.generation = generation;
        evoStats.bestSoFar = (Tree)in.readObject();
        population = (ArrayList<Tree>)in.readObject();

        in.close();

    }
    
}
