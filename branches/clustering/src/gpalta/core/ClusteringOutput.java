/*
 * ClusteringOutput.java
 *
 * Created on 1 de junio de 2006, 05:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gpalta.core;

/**
 *
 * @author neven
 */
public class ClusteringOutput extends Output
{
    public double[][] prototypes;
    public double[][] prob;
    
    public ClusteringOutput(int nArrays, int nSamples)
    {
        super(nArrays, nSamples);
    }
    
    public void setPrototypesCopy(double[][] proto)
    {
        prototypes = new double[proto.length][proto[0].length];
        for (int i = 0; i < proto.length; i++)
        {
            System.arraycopy(proto[i],0,prototypes[i],0,proto[i].length);
            /*for (int j = 0; j < proto[i].length; j++)
            {
                prototypes[i][j] = proto[i][j];
            }*/
        }
    }
    
    public void setPertenenceCopy(double[][] prob)
    {
        this.prob = new double[prob.length][prob[0].length];
        for (int i = 0; i < prob.length; i++)
        {
            System.arraycopy(prob[i],0,this.prob[i],0,prob[i].length);
            /*for (int j = 0; j < prob[i].length; j++)
            {
                this.prob[i][j] = prob[i][j];
            }*/
        }
    }
    
}
