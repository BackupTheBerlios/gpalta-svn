/*
 * Individual.java
 *
 * Created on 31 de mayo de 2006, 03:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gpalta.core;

/**
 *
 * @author neven
 */
public abstract class Individual
{
    private double fitness;
    private boolean isOnPop;
    
    public abstract int getSize();
    public abstract Individual deepClone();
    
    public double readFitness()
    {
        return fitness;
    }

    public void setFitness(double fit)
    {
        fitness = fit;
    }
    
    public void setOnPop(boolean flag)
    {
        isOnPop = flag;
    }

    public boolean isOnPop()
    {
        return isOnPop;
    }
    
}
