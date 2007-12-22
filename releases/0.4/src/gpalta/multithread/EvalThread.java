/*
 * EvalThread.java
 *
 * Created on 26-04-2007, 02:22:15 PM
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

class EvalThread extends Thread
{
    private Fitness fitness;
    private ProblemData problemData;
    private Individual[] inds;
    private TempVectorFactory tempVectorFactory;
    private Output tmpOutput;
    private int[] wSamples;
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

    public synchronized void eval(Individual[] inds, Output tmpOutput, int[] wSamples)
    {
        this.inds = inds;
        this.tmpOutput = tmpOutput;
        this.wSamples = wSamples;
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
                        fitResult = fitness.calculate(tmpOutput, inds[i], problemData, wSamples);
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