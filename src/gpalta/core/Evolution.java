/*
 * Evolution.java
 *
 * Created on 18 de mayo de 2005, 07:31 PM
 *
 * Copyright (C) 2005  Neven Boric <nboric@gmail.com>
 *
 * This file is part of GPalta.
 *
 * GPalta is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * GPalta is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPalta; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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
    private Fitness fitness;
    private DataHolder dataHolder;
    private PreviousOutputHolder previousOutputHolder;
    public int generation;
    public Config config;
    private NodeFactory nodeFactory;
    public EvolutionStats evoStats;
    private EvalVectors evalVectors;
    
    /**
     * Creates a new instance of Evolution, loading data from file
     * 
     * @param config The evolution configuration
     * @param initPop If true, the population is randomly initialized. Else, 
     * nothing is done (population will be later read from a file)
     */
    public Evolution(Config config, boolean initPop)
    {
        this.config = config;
        
        dataHolder = new DataHolder("data.txt");
        previousOutputHolder = new PreviousOutputHolder(config);
        nodeFactory = new NodeFactory(config, dataHolder);
        
        population = new ArrayList<Tree>();
        if (initPop)
        {
            treeBuilder = new TreeBuilder(config, nodeFactory);
            treeBuilder.build(population);
        }
        
        treeOp = new TreeOperator(config, nodeFactory);
        
        if( config.selectionMethod.equals("tournament"))
        {
            treeSelector = new TreeSelectorProportional(config);
        }
        else if (config.selectionMethod.equals("SUS"))
        {
            treeSelector = new TreeSelectorSUS(config);
        }
        else if (config.selectionMethod.equals("roulette"))
        {
            treeSelector = new TreeSelectorRoulette(config);
        }
        else if ( config.selectionMethod.equals("proportional"))
        {
            treeSelector = new TreeSelectorTournament(config);
        }
        else
        {
            treeSelector =new TreeSelectorTournament(config);
        }
        
        if (config.problemType.equals("classifier"))
        {
            fitness = new FitnessClassifier();
        }
        else if (config.problemType.equals("clustering"))
        {
            //fitness = new FitnessClustering();
        }
        else
        {
            fitness = new FitnessClassic();
        }
        
        fitness.init(config, dataHolder, "class.txt");

        evoStats = new EvolutionStats();
        if (initPop)
        {
            evoStats.bestSoFar = population.get(0);
        }
        
        generation = 0;
        
        
        if (config.nPreviousOutput == 0 && config.useVect)
        {
            evalVectors = new EvalVectors(dataHolder.nSamples);
        }
        
    }
    
    /**
     * Creates a new instance of Evolution, using the given data, desiredOutputs
     * and weights
     *
     * @param config The evolution configuration
     * @param data The current problem's data, where every row correponds to all
     * the samples for a variable.
     * @param desiredOutputs The desired outputs
     * @param weights The weight (importance) of each sample
     * @param initPop If true, the population is randomly initialized. Else, 
     * nothing is done (population will be later read from a file)
     */
    public Evolution(Config config, double[][] data, double[] desiredOutputs, double[] weights, boolean initPop)
    {
        this.config = config;
        
        dataHolder = new DataHolder(data);
        previousOutputHolder = new PreviousOutputHolder(config);
        nodeFactory = new NodeFactory(config, dataHolder);
        
        population = new ArrayList<Tree>();
        if (initPop)
        {
            treeBuilder = new TreeBuilder(config, nodeFactory);
            treeBuilder.build(population);
        }
        
        treeOp = new TreeOperator(config, nodeFactory);
        //treeSelector = new TreeSelector(config);
        if( config.selectionMethod.equals("proportional"))
        {
            treeSelector =new TreeSelectorProportional(config);
        }
        /*else if ( config.selectionMethod.equals("tournament"))
        {
            treeSelector =new TreeSelectorTournament(config);
        }*/
        else
        {
            treeSelector =new TreeSelectorTournament(config);
        }
        
        if (config.problemType.equals("classifier"))
        {
            fitness = new FitnessClassifier();
        }
        else if (config.problemType.equals("clustering"))
        {
            //fitness = new FitnessClustering();
        }
        else
        {
            fitness = new FitnessClassic();
        }
        fitness.init(config, dataHolder, desiredOutputs, weights);

        evoStats = new EvolutionStats();
        
        if (initPop)
        {
            evoStats.bestSoFar = population.get(0);
        }

        generation = 0;
        
        if (config.nPreviousOutput == 0 && config.useVect)
        {
            evalVectors = new EvalVectors(dataHolder.nSamples);
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
            if (!config.rememberLastEval || !t.fitCalculated)
            {
                fitness.calculate(t, evalVectors, dataHolder, previousOutputHolder);
                t.fitCalculated = true;
            }
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
     * Evaluate a single tree
     * 
     * @return The output of the Tree for every sample, or null if the Tree wasn't
     * evaluated
     */
    public synchronized double[] eval(Tree tree)
    {
        return fitness.calculate(tree, evalVectors, dataHolder, previousOutputHolder);
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
    
}
