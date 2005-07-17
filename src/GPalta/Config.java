/*
 * Config.java
 *
 * Created on 10 de mayo de 2005, 11:46 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package GPalta;

/**
 * Holds the GP parameters
 *
 * @author neven
 */
public abstract class Config 
{
    
    public static int populationSize = 500;
    public static int nGenerations = 1000;
    
    public static int tournamentSize = 2;
    
    public static int maxDepth = 9;

    /* Upper limits for the probability regions of the tree operations. This means:
     * probability of crossover = upLimitProbCrossOver - 0
     * probability of mutation = upLimitProbMutation - upLimitProbCrossOver
     * probability of reproduction = 1 - upLimitProbMutation
     */
    public static double upLimitProbCrossOver = 0.85;
    public static double upLimitProbMutation = 0.9;
    //The rest is for reproduction
    
    public static double constLowLimit = -100;
    public static double constUpLimit = 100;
    
    public static int maxCrossoverTries = 10;
    
    public static double upLimitProbSelectTerminal = .1;
    public static double upLimitProbSelectNonTerminal = 1;
    public static double upLimitProbSelectRoot = 0;
    //The rest is for select any node
    
    public static double probGrowBuild = .5;
    //The rest is for Full Build
    
    public static int nPreviousOutput = 0;

    public static String saveFileName = "evo.bin";
    
    public static String logFileName = "log.txt";
    
    public static boolean useVect = false;
}
