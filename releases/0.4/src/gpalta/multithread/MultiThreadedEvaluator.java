/*
 * MultiThreadedEvaluator.java
 *
 * Created on 17-04-2007, 04:14:33 PM
 *
 * Copyright (C) 2007 Neven Boric <nboric@gmail.com>
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

package gpalta.multithread;

import gpalta.core.*;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MultiThreadedEvaluator
{
    private int nThreads;
    private Queue<EvalThread> availableThreads;

    public MultiThreadedEvaluator(Config config, Fitness f, ProblemData problemData)
    {
        nThreads = config.nEvalThreads;
        Fitness[] fitness = new Fitness[nThreads];
        TempVectorFactory[] tempVectorFactory = new TempVectorFactory[nThreads];
        for (int i=0; i< nThreads; i++)
        {
            try
            {
                fitness[i] = (Fitness)f.clone();
                fitness[i].setCallingThread(i);
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

    public synchronized void eval(List<? extends Individual> population, Output tmpOutput, int[] wSamples)
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
                evalThread.eval(inds, (Output)tmpOutput.clone(), wSamples);
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