/*
 * TreeFitnessComparator.java
 *
 * Created on 29 de marzo de 2006, 04:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gpalta.core;

import java.util.*;
import gpalta.nodes.*;
/**
 *
 * @author DSP
 */


public class TreeFitnessComparator implements Comparator<Tree>
{    
  
    public int compare(Tree o1, Tree o2) {
        if (o1.fitness < o2.fitness)
        {
            return(-1);
        }
        else if (o1.fitness>o2.fitness)
        {
            return(1);
        }
        else
        {
            return(0);
        }
               
    }
    
}
