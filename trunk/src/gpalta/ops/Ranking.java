/*
 * Ranking.java
 *
 * Created on 31 de marzo de 2006, 11:14 AM
 *
 * Copyright (C) 2006 Juan Ramirez <tiomemo@gmail.com>
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
import java.util.*;

/**
 * Implements tree sorting methods for selection
 */
public abstract class Ranking {
    
    
    public int popSize;
    public boolean filled;
    public List<Tree> population;
    public double[] adjustedFitness;
    public Tree [] popArray;
    public double min;
    public double max;
    public double totalFitness;
    public double  acumulatedFit[];
    
    public abstract void rankPop(List<Tree> population, Comparator comp);
    
    public void init(List<Tree> population, Comparator comp)
    {
        this.population = population;
        popArray = this.treesSort(population, comp);
        this.popSize = population.size();
        min=popArray[0].fitness;
        max=popArray[ popSize -1].fitness;
        adjustedFitness = new double [popSize];
        acumulatedFit = new double [popSize];
        filled=true;
    }
    
    public Tree[] treesSort(List<Tree> population, Comparator comp)
    {
        Tree [] popArray = new Tree[population.size()];
        
        /*
         * Move population to an Array structure for sorting. 
         */        
        for(int i=0;i<population.size();i++)
        {
            popArray[i]=population.get(i);
            population.get(i).isOnPop=false;
        }
        
        /*
         *Sort
         */
        Arrays.sort(popArray, comp);
        return(popArray);
    }
    
}
