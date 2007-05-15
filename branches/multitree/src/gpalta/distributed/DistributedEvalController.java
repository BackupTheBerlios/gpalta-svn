package gpalta.distributed;

import gpalta.core.*;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: nvn Date: 17-04-2007 Time: 04:14:33 PM To change this template
 * use File | Settings | File Templates.
 */
public class DistributedEvalController
{
    private boolean first;
    private boolean allClientsDone;
    private boolean[] clientDone;
    private int nClients;
    private DistributedEvalClient[] evalClient;
    private double[][][] fitResult;
    private Fitness fitness;

    public DistributedEvalController(Config config, Fitness fitness, ProblemData problemData, Output tmpOutput)
    {
        this.fitness = fitness;
        String separator = "\\s*,\\s*";
        String[] servers = config.distributedServers.split(separator);
        nClients = servers.length;
        evalClient = new DistributedEvalClient[nClients];
        for (int i=0; i<nClients; i++)
        {
            evalClient[i] = new DistributedEvalClient(i, this, servers[i], config.distributedPort, fitness, problemData, tmpOutput);
        }
        first = true;
        clientDone = new boolean[nClients];
        for (int i=0; i<nClients; i++)
        {
            clientDone[i] = false;
        }
        allClientsDone = false;
        fitResult = new double[nClients][][];
    }

    public synchronized void notifyReady(int wClient)
    {
        clientDone[wClient] = true;
        allClientsDone = true;
        for (int i=0; i<nClients; i++)
        {
            if (!clientDone[i])
            {
                allClientsDone = false;
                break;
            }
        }
        notifyAll();
    }



    public synchronized <T extends Individual> void eval(List<T> population)
    {
        if (first)
        {
            first = false;
            allClientsDone = false;
            for (int i=0; i<nClients; i++)
            {
                clientDone[i] = false;
                evalClient[i].start();
            }
            while(!allClientsDone)
            {
                try
                {
                    wait();
                }
                catch (InterruptedException e1)
                {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
        allClientsDone = false;
        int[][] individualsPerClient = new int[nClients][2];
        for (int i = 0; i < nClients; i++)
        {
            if (i==nClients-1)
            {
                individualsPerClient[i][0] = i*population.size()/nClients;
                individualsPerClient[i][1] = population.size();
            }
            else
            {
                individualsPerClient[i][0] = i*population.size()/nClients;
                individualsPerClient[i][1] = (i+1)*population.size()/nClients;
            }
        }
        Individual[][] inds = new Individual[nClients][0];
        for (int i=0; i<nClients; i++)
        {
            clientDone[i] = false;
            inds[i] = population.subList(individualsPerClient[i][0], individualsPerClient[i][1]).toArray(inds[i]);
            fitResult[i] = new double[individualsPerClient[i][1]-individualsPerClient[i][0]][];
            evalClient[i].eval(fitResult[i], inds[i]);
        }
        while(!allClientsDone)
        {
            try
            {
                wait();
            }
            catch (InterruptedException e1)
            {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        for (int i=0; i<nClients; i++)
        {
            for (int j=0; j<inds[i].length; j++)
            {
                fitness.assign(population.get(individualsPerClient[i][0]+j), fitResult[i][j]);
            }
        }
    }
}
