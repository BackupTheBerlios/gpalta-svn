/*
 * DistributedEvalClient.java
 *
 * Created on 16-04-2007, 03:00:42 PM
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

package gpalta.distributed;

import gpalta.core.Individual;
import gpalta.core.Output;
import gpalta.core.ProblemData;
import gpalta.core.Fitness;

import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DistributedEvalClient extends Thread implements DistributedEvalProtocol
{
    private String host;
    private int port;
    private Individual[] inds;
    private double[][] fitResult;
    private Fitness fitness;
    private ProblemData problemData;
    private Output tmpOutput;
    private boolean first;
    private DistributedEvalController evalController;
    private int wThreadAmI;

    public DistributedEvalClient(int wThreadAmI, DistributedEvalController evalController, String host, int port, Fitness fitness, ProblemData problemData, Output tmpOutput)
    {
        this.wThreadAmI = wThreadAmI;
        this.evalController = evalController;
        this.host = host;
        this.port = port;
        this.fitness = fitness;
        this.problemData = problemData;
        this.tmpOutput = tmpOutput;
        first = true;
    }

    public synchronized void eval(double[][] fitResult, Individual[] inds)
    {
        this.inds = inds;
        this.fitResult = fitResult;
        notifyAll();
    }

    public synchronized void notifyReady()
    {
        evalController.notifyAll();
    }


    public synchronized void run()
    {
        Socket socket;
        ObjectInputStream in;
        ObjectOutputStream out;
        while(true)
        {
            try
            {
                System.out.println("Client " + wThreadAmI + ": Establishing connection with " + host +  ":" + port + "...");
                socket = new Socket(host, port);
                out = new ObjectOutputStream(socket.getOutputStream());
                System.out.println("Client " + wThreadAmI + " Done");
                if (first)
                {
                    System.out.println("Client " + wThreadAmI + ": Sending initial data...");
                    out.writeUTF(INIT_STRING);
                    first = false;
                    out.writeObject(fitness);
                    out.writeObject(problemData);
                    out.writeObject(tmpOutput);
                    out.close();
                    System.out.println("Client " + wThreadAmI + " Done");
                }
                else
                {
                    out.writeUTF(EVAL_STRING);
                    System.out.println("Client " + wThreadAmI + ": Sending " + inds.length + " individuals...");
                    out.writeInt(inds.length);
                    for (int i = 0; i < inds.length; i++)
                    {
                        out.writeObject(inds[i]);
                    }
                    System.out.println("Client " + wThreadAmI + " Done");
                    System.out.println("Client " + wThreadAmI + ": Receiving results from server...");
                    in = new ObjectInputStream(socket.getInputStream());
                    for (int i = 0; i < inds.length; i++)
                    {
                        fitResult[i] = (double[]) in.readObject();
                    }
                    out.close();
                    in.close();
                    System.out.println("Client " + wThreadAmI + " Done");
                }
                socket.close();
                evalController.notifyReady(wThreadAmI);
                wait();
            }
            catch (IOException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
