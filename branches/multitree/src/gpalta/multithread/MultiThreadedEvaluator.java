package gpalta.multithread;

import gpalta.core.*;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by IntelliJ IDEA. User: nvn Date: 17-04-2007 Time: 04:14:33 PM To change this template
 * use File | Settings | File Templates.
 */
public class MultiThreadedEvaluator
{
    private int nThreads;
    private Fitness[] fitness;
    private TempVectorFactory[] tempVectorFactory;
    private Queue<EvalThread> availableThreads;

    public MultiThreadedEvaluator(Config config, Fitness f, ProblemData problemData)
    {
        nThreads = config.nEvalThreads;
        this.fitness = new Fitness[nThreads];
        tempVectorFactory = new TempVectorFactory[nThreads];
        for (int i=0; i< nThreads; i++)
        {
            try
            {
                this.fitness[i] = (Fitness)f.clone();
            }
            catch (CloneNotSupportedException e)
            {
                Logger.log(e);
            }
            tempVectorFactory[i] = new TempVectorFactory(problemData.nSamples);
        }
        availableThreads = new ConcurrentLinkedQueue<EvalThread>();
        for (int i=0; i< nThreads; i++)
        {
            EvalThread e = new EvalThread(this, config, fitness[i], problemData, tempVectorFactory[i]);
            e.start();
            availableThreads.add(e);
        }
    }

    public synchronized void notifyReady(EvalThread evalThread)
    {
        availableThreads.add(evalThread);
        notifyAll();
    }

    public synchronized <T extends Individual> void eval(List<T> population, Output tmpOutput)
    {
        int nDone = 0;
        EvalThread evalThread;
        while(nDone < population.size())
        {
            while((evalThread = availableThreads.poll())==null)
            {
                try
                {
                    wait();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            Individual[] inds = new Individual[0];
            int nToDo = 5;
            if (nDone+nToDo > population.size())
            {
                nToDo = population.size()-nDone;
            }
            inds = population.subList(nDone, nDone+nToDo).toArray(inds);
            try
            {
                evalThread.eval(inds, (Output)tmpOutput.clone());
            }
            catch (CloneNotSupportedException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            nDone += nToDo;
        }
        /* wait for the last threads */
        while(availableThreads.size()<nThreads)
        {
            try
            {
                wait();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        /*
        for (int i=0; i<population.size(); i++)
        {
            if (!population.get(i).fitCalculated)
            {
                System.out.println("i = " + i);
            }
        }
        */
    }

    public void destroy()
    {
        EvalThread evalThread;
        while((evalThread = availableThreads.poll())!=null)
            evalThread.exit();
    }

    protected void finalize()
    {
        try
        {
            super.finalize();
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}