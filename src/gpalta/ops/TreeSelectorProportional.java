/*
 * TreeSelectorProportional.java
 *
 * Created on 28 de marzo de 2006, 06:03 PM
 *
 * Copyright (C) 2006  Juan Ramirez <tiomemo@gmail.com>
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
package gpalta.ops;
import gpalta.nodes.*;
import gpalta.core.*;
import java.util.*;

/**
 * Implements proportional selection. Assumes populationSize mod tournamentSize = 0
 * @author DSP
 */
public class TreeSelectorProportional extends TreeSelector
{
    private Config config;
    private Comparator<Tree> comp;
    private Comparator<Tree> comp2;
    
    /** Creates a new instance of TreeSelectorProportional */
    public TreeSelectorProportional(Config config)
    {
        this.config = config;
        this.comp=new TreeFitnessComparator();
        this.comp2=new TreeFitnessComparatorDec();
    }
    
    /**
     * Performs the selection
     * 
     * @param population A list of Trees from where to select the individuals
     * 
     * @return A new list of Trees with the selected individuals. If a Tree is
     * selected more than once, each instance of that Tree will be a totally
     * independant individual (no other Trees will be modified when modifying that Tree)
     *
     */
    public List<Tree> select(List<Tree> population)
    {
        double fitness;
        double totalFitness;
        int treeCounter, treeCopies,k;
        Tree temp1, temp2;
        
        Tree [] popArray = new Tree[population.size()];        
        List<Tree> out = new ArrayList<Tree>();
        
        
        
        /*
         * Move population to an Array structure for sorting. Calculate totalFitness also.
         */
        totalFitness=0;
        for(int i=0;i<population.size();i++)
        {
            fitness=population.get(i).fitness;
            totalFitness+=fitness;
        
            popArray[i]=population.get(i);
        }
        
        /*
         *Sort
         */
        Arrays.sort(popArray, comp);
        
        /*
         *Select a number of copies for each individual equal to the truncated copy expectance
         */
        treeCounter=0;
        for (int i=0;i<population.size();i++)
        {
            fitness=population.get(i).fitness;
            temp1 = (Tree)(popArray[ population.size()-1-i ]).deepClone(-1);
            treeCopies = (int)Math.floor(fitness/totalFitness*population.size());
            for (int j=1; j<=treeCopies;j++ )
            {
                out.add( (Tree)temp1.deepClone(-1) );
            }
            treeCounter+=treeCopies;
        }
        
        k=0;
        
        /*
         *Select best fitted for unsigned population slots
         */
        Arrays.sort(popArray, comp2);
        while(treeCounter<population.size())
        {
            out.add((Tree)(popArray[ population.size()-1-k ]).deepClone(-1));
            k++;
            treeCounter++;
        }
        
        return(out);
    }
    
    
}

class TreeFitnessComparatorDec implements Comparator<Tree>
{    
  
    public int compare(Tree o1, Tree o2) {
        double f1=o1.fitness-Math.floor(o1.fitness);
        double f2=o2.fitness-Math.floor(o2.fitness);
        
        if (f1 < f2)
        {
            return(-1);
        }
        else if (f1>f2)
        {
            return(1);
        }
        else
        {
            return(0);
        }
               
    }
    
}