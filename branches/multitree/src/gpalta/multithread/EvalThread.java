package gpalta.multithread;

import gpalta.core.*;

/**
 * Created by IntelliJ IDEA. User: nvn Date: 26-04-2007 Time: 02:22:15 PM To change this template
 * use File | Settings | File Templates.
 */
class EvalThread extends Thread
{
    private Fitness fitness;
    private ProblemData problemData;
    private Individual[] inds;
    private TempVectorFactory tempVectorFactory;
    private Output tmpOutput;
    private Config config;
    private boolean exit;
    private MultiThreadedEvaluator threadedEvaluator;

    public EvalThread(MultiThreadedEvaluator threadedEvaluator, Config config, Fitness fitness, ProblemData problemData, TempVectorFactory tempVectorFactory)
    {
        this.threadedEvaluator = threadedEvaluator;
        this.config = config;
        this.fitness = fitness;
        this.problemData = problemData;
        this.tempVectorFactory = tempVectorFactory;
        exit = false;
    }

    public synchronized void eval(Individual[] inds, Output tmpOutput)
    {
        this.inds = inds;
        this.tmpOutput = tmpOutput;
        notifyAll();
    }

    public synchronized void run()
    {
        while (!exit)
        {
            if (inds != null)
            {
                double[] fitResult;
                for (int i = 0; i < inds.length; i++)
                {
                    if (!config.rememberLastEval || !inds[i].fitCalculated)
                    {
                        inds[i].evalVect(tmpOutput, tempVectorFactory, problemData);
                        fitResult = fitness.calculate(tmpOutput, inds[i], problemData);
                        fitness.assign(inds[i], fitResult);
                        inds[i].fitCalculated = true;
                    }
                }
                threadedEvaluator.notifyReady(this);
            }
            try
            {
                wait();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public synchronized void exit()
    {
        exit = true;
        notifyAll();
    }
}