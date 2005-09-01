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
    
    public TreeBuilder treeBuilder;
    public List<Tree> population;
    public TreeOperator treeOp;
    public TreeSelector treeSelector;
    public Fitness fitness;
    public RealDataHolder realDataHolder;
    public LogicDataHolder logicDataHolder;
    public int generation;
    
    public List<double[]> realEvalVectors;
    public List<boolean[]> logicEvalVectors;
    private int currentRealEvalVector;
    private int currentLogicEvalVector;

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
        
        
        if (Config.nPreviousOutput == 0 && Config.useVect)
        {
            initEvalVectors();
        }
        
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
        
        if (Config.nPreviousOutput == 0 && Config.useVect)
        {
            initEvalVectors();
        }
        
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
    
    private void initEvalVectors()
    {
        realEvalVectors = new ArrayList<double[]>(0);
        logicEvalVectors = new ArrayList<boolean[]>(0);
        currentRealEvalVector = -1;
        currentLogicEvalVector = -1;
    }
    
    public double[] getRealEvalVector()
    {
        currentRealEvalVector++;
        if (currentRealEvalVector == realEvalVectors.size())
        {
            //Logger.log("Adding new realEvalVector, " + currentRealEvalVector);
            realEvalVectors.add(new double[realDataHolder.nSamples]);
        }
        return realEvalVectors.get(currentRealEvalVector);
    }
    
    public boolean[] getLogicEvalVector()
    {
        
        currentLogicEvalVector++;
        if (currentLogicEvalVector == logicEvalVectors.size())
        {
            //Logger.log("Adding new logicEvalVector, " + currentLogicEvalVector);
            logicEvalVectors.add(new boolean[realDataHolder.nSamples]);
        }
        return logicEvalVectors.get(currentLogicEvalVector);
    }
    
    public void releaseRealEvalVector()
    {
        currentRealEvalVector--;
    }
    
    public void releaseLogicEvalVector()
    {
        currentLogicEvalVector--;
    }
    
}
