/*
 * TreeSelector.java
 *
 * Created on 19 de mayo de 2005, 05:20 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package ops;
import java.util.*;
import nodes.*;
import GPalta.*;

/**
 * Implements Tournament selection. Assumes populationSize mod tournamentSize = 0
 * @author DSP
 */
public class TreeSelector
{
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
        List<Tree> out = new ArrayList<Tree>();
        for (Tree t: population)
        {
            t.isOnPop = false;
        }
        double maxFit;
        int indMaxFit;
        //For every pass:
        for (int i=0; i<Config.tournamentSize; i++)
        {
            int[] perm = Common.randPerm(Config.populationSize);
            
            //For every tournament:
            for (int j=0; j<Config.populationSize; j+= Config.tournamentSize)
            {
                maxFit = population.get(perm[j]).fitness;
                indMaxFit = j;
                
                //For every tree in the tournament:
                for (int k=j+1; k<j+Config.tournamentSize; k++)
                {
                    if (population.get(perm[k]).fitness > maxFit)
                    {
                        maxFit = population.get(perm[k]).fitness;
                        indMaxFit = k;
                    }
                }
                
                /* If this tree has already been selected, we need a copy of it
                 * (So we don't modify both when operating on one of them)
                 */
                if (population.get(perm[indMaxFit]).isOnPop)
                {
                    Tree tmp = (Tree)population.get(perm[indMaxFit]).deepClone(-1);
                    tmp.isOnPop = true;
                    out.add(tmp);
                }
                else
                {
                    population.get(perm[indMaxFit]).isOnPop = true;
                    out.add(population.get(perm[indMaxFit]));
                }
            }
        }
        return out;
    }
    
}
