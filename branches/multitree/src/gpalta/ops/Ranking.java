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

import gpalta.core.*;

import java.util.*;

/**
 * Implements tree sorting methods for selection
 */
public abstract class Ranking
{


    int popSize;
    boolean filled;
    public double[] adjustedFitness;
    double min;
    double max;
    public double totalFitness;
    public double acumulatedFit[];

    public abstract <T extends Individual> List<T> rankPop(List<T> population, Comparator<Individual> comp);

    public <T extends Individual> List<T> init(List<T> population, Comparator<Individual> comp)
    {
        List<T> popList = this.indSort(population, comp);
        this.popSize = population.size();
        min = popList.get(0).readFitness();
        max = popList.get(popSize - 1).readFitness();
        adjustedFitness = new double [popSize];
        acumulatedFit = new double [popSize];
        filled = true;
        return popList;
    }

    private <T extends Individual> List<T> indSort(List<T> population, Comparator<Individual> comp)
    {
        List<T> list = new ArrayList<T>(population.size());
        list.addAll(population);
        for (Individual ind : list)
            ind.setOnPop(true);

        Collections.sort(list, comp);
        return list;
//        Individual [] popArray = new Individual[population.size()];
//
//        /*
//        * Move population to an Array structure for sorting.
//        */
//        for (int i = 0; i < population.size(); i++)
//        {
//            popArray[i] = population.get(i);
//            population.get(i).setOnPop(false);
//        }
//
//        /*
//        *Sort
//        */
//        Arrays.sort(popArray, comp);
//        return (T[]) popArray;
    }

}
