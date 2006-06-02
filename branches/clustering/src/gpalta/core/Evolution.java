/*
 * Evolution.java
 *
 * Created on 18 de mayo de 2005, 07:31 PM
 *
 * Copyright (C) 2005, 2006 Neven Boric <nboric@gmail.com>
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
    public Population population;
    private TreeOperator treeOp;
    private TreeSelector treeSelector;
    private Ranking theRanking;
    public Fitness fitness;
    private DataHolder dataHolder;
    private PreviousOutputHolder previousOutputHolder;
    public int generation;
    public Config config;
    private NodeFactory nodeFactory;
    public EvolutionStats evoStats;
    private EvalVectors evalVectors;
    
    private void initCommon(Config config, DataHolder initializedData, boolean initPop)
    {
        previousOutputHolder = new PreviousOutputHolder(config);
        nodeFactory = new NodeFactory(config, initializedData);

        treeOp = new TreeOperator(config, nodeFactory);
        
        treeBuilder = new TreeBuilder(config, nodeFactory);
        
        if( config.selectionMethod.equals("tournament"))
        {
            treeSelector = new TreeSelectorTournament(config);
        }
        else
        {
            if (config.rankingType.equals("Raw"))
            {
                theRanking = new RankingRaw();
            }
            else if (config.rankingType.equals("LFR"))
            {
                theRanking = new RankingLFR();
            }
            else
            {
                theRanking = new RankingLFR();
            }
            
            
            if (config.selectionMethod.equals("SUS"))
            {
              treeSelector = new TreeSelectorSUS(config, theRanking);
            }
            else if (config.selectionMethod.equals("roulette"))
            {
              treeSelector = new TreeSelectorRoulette(config, theRanking);
            }
            else if ( config.selectionMethod.equals("proportional"))
            {
                treeSelector = new TreeSelectorProportional(config, theRanking);
            }
            else
            {
            treeSelector =new TreeSelectorTournament(config);
            }
        }
        
        
        if (config.fitness.equals("classifier"))
        {
            fitness = new FitnessClassifier();
        }
        else if (config.fitness.equals("clustering"))
        {
            if (config.useSoftPertenence)
                fitness = new FitnessClusteringFuzzy();
            else
                fitness = new FitnessClustering();
        }
        else
        {
            fitness = new FitnessClassic();
        }
        
        if (config.nPreviousOutput == 0 && config.useVect)
        {
            evalVectors = new EvalVectors(dataHolder.nSamples);
        }
        
        evoStats = new EvolutionStats();
        if (initPop)
        {
            population = new SingleTreePopulation();
            population.init(config, initializedData, treeBuilder);
            evoStats.bestSoFar = population.get(0);
        }
        
        generation = 0;
        
    }
    
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
        
        initCommon(config, dataHolder, initPop);
        
        fitness.init(config, dataHolder, "class.txt");
        
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
        
        initCommon(config, dataHolder, initPop);
        
        Output des = new Output(1, dataHolder.nSamples);
        des.setArray(0, desiredOutputs);
        fitness.init(config, dataHolder, des, weights);
        
    }
    
    /**
     * Evaluate the current population.
     */
    public synchronized void eval()
    {
        population.eval(fitness, evalVectors, dataHolder, previousOutputHolder);
        Individual bestThisGen = population.get(0);
        evoStats.avgFit = 0;
        evoStats.avgNodes = 0;
        
        for (int i=0; i<config.populationSize; i++)
        {
            Individual ind = population.get(i);
            evoStats.avgFit += ind.readFitness();
            evoStats.avgNodes += ind.getSize();
            if (ind.readFitness() > bestThisGen.readFitness())
                bestThisGen = ind;
        }
        
        evoStats.bestTreeChanged = false;
        if (bestThisGen.readFitness() > evoStats.bestSoFar.readFitness())
        {
            evoStats.bestSoFar = bestThisGen.deepClone();
            evoStats.bestTreeChanged = true;
        }

        evoStats.generation = generation;
        evoStats.bestFitThisGen = bestThisGen.readFitness();
    }
    
    /**
     * Evaluate a single tree
     * 
     * @return The output of the Tree for every sample, or null if the Tree wasn't
     * evaluated
     */
    public synchronized Output getRawOutput(Individual ind)
    {
        return population.getRawOutput(ind, evalVectors, dataHolder, previousOutputHolder);
    }
    
    /**
     * Evaluate a single tree
     * 
     * @return The output of the Tree for every sample, or null if the Tree wasn't
     * evaluated
     */
    public synchronized Output getProcessedOutput(Individual ind)
    {
        return population.getProcessedOutput(ind, fitness, evalVectors, dataHolder, previousOutputHolder);
    }
    
    /**
     * Evolve one generation. Assumes the current population is already evaluated
     * and doesn't evaluate the evolved one
     */
    public synchronized void evolve()
    {
        population.doSelection(treeSelector);
        population.evolve(treeOp);
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
        Logger.log("with fitness: " + evoStats.bestSoFar.readFitness());
        
        
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
        population = (Population)in.readObject();

        in.close();

    }
    
}
