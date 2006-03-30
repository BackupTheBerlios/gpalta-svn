/*
 * TreeSelectorSUS.java
 *
 * Created on 30 de marzo de 2006, 03:28 PM
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
 *
 * @author DSP
 */
public class TreeSelectorSUS extends TreeSelector
{
    private Config config;
    private Comparator<Tree> comp;
    private double pointerDistance;
    
    /** Creates a new instance of TreeSelectorRoulette */
    public TreeSelectorSUS(Config config) {
        this.config = config;
        this.comp=new TreeFitnessComparator();
        this.pointerDistance = config.pointerDistance;
    }
    
     public List<Tree> select(List<Tree> population)
    {
        double totalFitness,fitness,pointerPos,range,min;
        int k;
        
        Tree [] popArray = new Tree[population.size()];
        List<Tree> out = new ArrayList<Tree>();
        
        double [] acumulatedFit = new double [population.size()];
        
        
        /*
         * Move population to an Array structure for sorting. 
         */
        
        for(int i=0;i<population.size();i++)
        {
            popArray[i]=population.get(i);
        }
        
        /*
         *Sort
         */
        Arrays.sort(popArray, comp);
        
        /*
         *Adjust fitness measures to positive values, these changes must be 
         *also reflected on acumulated fitness
         */
        min=popArray[0].fitness;
                
        /*
         *Calculate acumalated fitness and acumulated probabilities
         */
        totalFitness=0;
        for(int i=0;i<population.size();i++)
        {
            fitness=popArray[i].fitness - min;
            totalFitness+=fitness;
            acumulatedFit[i] = totalFitness;
        }
        
                
        /*
         *SUS iterations
         */
        pointerPos=Math.random();
        k=0;
        for(int i=0; i<population.size();i++)
        {
            while ( pointerPos >=acumulatedFit[k]/totalFitness)
             {
                 k++;
                 if( k==population.size()-1 )
                 {
                     break;
                 }
             }
            out.add( (Tree)(popArray[ k ]).deepClone(-1) );
            pointerPos+=pointerDistance;
            if (pointerPos>1)
            {
                pointerPos=pointerPos-1;
                k=0;
            }
            else if ( k >= population.size() )
            {
                k=0;
            }
            
         }
       
        return(out);
     }
    
}
