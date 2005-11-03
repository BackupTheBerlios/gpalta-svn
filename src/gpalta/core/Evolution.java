/*
 * Evolution.java
 *
 * Created on 18 de mayo de 2005, 07:31 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package gpalta.core;
import gpalta.nodes.*;
import gpalta.ops.*;
import java.util.*;
import java.io.*;


/**
 * Holds the population and performs evolution
 * @author neven
 */
public class Evolution
{
    
    private TreeBuilder treeBuilder;
    public List<Tree> population;
    private TreeOperator treeOp;
    private TreeSelector treeSelector;
    public Fitness fitness;
    public DataHolder dataHolder;
    public PreviousOutputHolder previousOutputHolder;
    public int generation;
    public Config config;
    public NodeTypesConfig types;
    
    public List<double[]> evalVectors;
    private int currentEvalVector;

    public EvolutionStats evoStats;
    
    /**
     * Creates a new instance of Evolution, loading data from file
     * 
     * @param initPop If true, the population is randomly initialized. Else, 
     * nothing is done (population will be later read from a file)
     */
    public Evolution(Config config, boolean initPop)
    {
        this.config = config;
        
        dataHolder = new DataHolder("data.txt");
        previousOutputHolder = new PreviousOutputHolder(config);
        types = new NodeTypesConfig(this);
        
        population = new ArrayList<Tree>();
        if (initPop)
        {
            treeBuilder = new TreeBuilder(config, types);
            treeBuilder.build(population);
        }
        
        treeOp = new TreeOperator(config, types);
        treeSelector = new TreeSelector(config);
        fitness = new FitnessClassifier();
        fitness.init(this, "class.txt");

        evoStats = new EvolutionStats();
        if (initPop)
        {
            evoStats.bestSoFar = population.get(0);
        }
        
        generation = 0;
        
        
        if (config.nPreviousOutput == 0 && config.useVect)
        {
            initEvalVectors();
        }
        
    }
    
    /**
     * Creates a new instance of Evolution, using the given data, desiredOutputs and weights
     */
    public Evolution(Config config, double[][] data, double[] desiredOutputs, double[] weights, boolean initPop)
    {
        this.config = config;
        
        dataHolder = new DataHolder(data);
        previousOutputHolder = new PreviousOutputHolder(config);
        types = new NodeTypesConfig(this);
        
        population = new ArrayList<Tree>();
        if (initPop)
        {
            treeBuilder = new TreeBuilder(config, types);
            treeBuilder.build(population);
        }
        
        treeOp = new TreeOperator(config, types);
        treeSelector = new TreeSelector(config);
        fitness = new FitnessClassic();
        fitness.init(this, desiredOutputs, weights);

        evoStats = new EvolutionStats();
        
        if (initPop)
        {
            evoStats.bestSoFar = population.get(0);
        }

        generation = 0;
        
        if (config.nPreviousOutput == 0 && config.useVect)
        {
            initEvalVectors();
        }
        
    }
    
    /**
     * Evaluate the current population.
     */
    public synchronized void eval()
    {
        Tree bestThisGen = population.get(0);
        evoStats.avgFit = 0;
        evoStats.avgNodes = 0;
        
        for (Tree t : population)
        {
            fitness.calculate(t);
            evoStats.avgFit += t.fitness;
            evoStats.avgNodes += t.nSubNodes;
            if (t.fitness > bestThisGen.fitness)
            {
                bestThisGen = t;
            }
        }
        
        evoStats.avgFit /= config.populationSize;
        evoStats.avgNodes /= config.populationSize;
        
        evoStats.bestTreeChanged = false;
        if (bestThisGen.fitness > evoStats.bestSoFar.fitness)
        {
            evoStats.bestSoFar = (Tree)bestThisGen.deepClone(-1);
            evoStats.bestTreeChanged = true;
        }

        evoStats.generation = generation;
        evoStats.bestFitThisGen = bestThisGen.fitness;
    }
    
    /**
     * Evolve one generation. Assumes the current population is already evaluated
     * and doesn't evaluate the evolved one
     */
    public synchronized void evolve()
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
    public synchronized void save(String fileName) throws IOException
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
    public synchronized void read(String fileName) throws IOException, ClassNotFoundException
    {
        FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream in = new ObjectInputStream(fis);
        
        generation = in.readInt();
        evoStats.generation = generation;
        evoStats.bestSoFar = (Tree)in.readObject();
        population = (ArrayList<Tree>)in.readObject();

        in.close();

    }
    
    private synchronized void initEvalVectors()
    {
        evalVectors = new ArrayList<double[]>(0);
        currentEvalVector = -1;
    }
    
    public synchronized double[] getEvalVector()
    {
        currentEvalVector++;
        if (currentEvalVector == evalVectors.size())
        {
            //Logger.log("Adding new realEvalVector, " + currentRealEvalVector);
            evalVectors.add(new double[dataHolder.nSamples]);
        }
        return evalVectors.get(currentEvalVector);
    }
            
    public synchronized void releaseEvalVector()
    {
        currentEvalVector--;
    }
    
}
