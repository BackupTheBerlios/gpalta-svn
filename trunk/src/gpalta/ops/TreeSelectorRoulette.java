/*
 * TreeSelectorRoulette.java
 *
 * Created on 30 de marzo de 2006, 12:45 PM
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
import gpalta.core.*;
import java.util.*;

/**
 * Implements roulette selection method
 */
public class TreeSelectorRoulette extends TreeSelector
{
    private Config config;
    private Comparator<Tree> comp;
    private Ranking theRanking;
    
    /** Creates a new instance of TreeSelectorRoulette */
    public TreeSelectorRoulette(Config config, Ranking theRanking) {
        this.config = config;
        this.comp=new TreeFitnessComparator();
        this.theRanking = theRanking;
    }
    
    /*
     * TODO: arrange order to descendant on theRanking 
     */
     public List<Tree> select(List<Tree> population)
    {
        double randomNumber;
        int k;
        Tree temp1;
        
        
        List<Tree> out = new ArrayList<Tree>();
        theRanking.rankPop(population, comp);
        
        /*
         * Roulette iterations
         */
        for(int i=0; i<population.size();i++)
        {
             randomNumber=Math.random();
             k=0;
             while ( theRanking.acumulatedFit[k]/theRanking.totalFitness <= randomNumber)
             {
                 k++;
                 if( k==population.size()-1 )
                 {
                     break;
                 }
             }
             temp1=(theRanking.popArray[ k ]);
             if (!temp1.isOnPop)
             {
                out.add(temp1);
                temp1.isOnPop=true;
             }
             else
             {
                out.add( (Tree)temp1.deepClone(-1) );
             }
             
             
        }
       
        return(out);
     }
    
}
