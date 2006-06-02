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
public interface Individual
{
    double readFitness();
    void setFitness(double fit);
    int getSize();
    Individual deepClone();
    
}
