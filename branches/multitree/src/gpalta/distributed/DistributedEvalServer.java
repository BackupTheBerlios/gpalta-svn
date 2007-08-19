package gpalta.distributed;

import gpalta.core.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA. User: nvn Date: 15-04-2007 Time: 05:13:44 PM To change this template
 * use File | Settings | File Templates.
 */
public class DistributedEvalServer implements DistributedEvalProtocol
{
    private Config config;
    private Fitness fitness;
    private ProblemData problemData;
    private Output tmpOutput;
    private TempVectorFactory tempVectorFactory;
    private boolean first;

    public DistributedEvalServer(Config config)
    {
        this.config=config;
        first = true;
    }

    public void init(ObjectInputStream in, ObjectOutputStream out)
    {
        try
        {
            System.out.print("Receiving initial data...");
            fitness = (Fitness) in.readObject();
            problemData = (ProblemData) in.readObject();
            tmpOutput = (Output) in.readObject();
            System.out.println("Done");
            tempVectorFactory = new TempVectorFactory(problemData.nSamples);
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public void eval(ObjectInputStream in, ObjectOutputStream out)
    {
        try
        {
            int nIndividuals = in.readInt();
            System.out.print("Receiving " + nIndividuals + " individuals...");
            Individual[] ind = new Individual[nIndividuals];
            for (int i=0; i<nIndividuals; i++)
            {
                ind[i] = (Individual) in.readObject();
            }
            System.out.println("Done");
            System.out.print("Evaluating...");
            double[][] fitResult = new double[nIndividuals][];
            for (int i = 0; i < ind.length; i++)
            {
                ind[i].evalVect(tmpOutput, tempVectorFactory, problemData);
                fitResult[i] = fitness.calculate(tmpOutput, ind[i], problemData);
            }
            System.out.println("Done");
            System.out.print("Sending results back to client...");
            for (int i = 0; i < ind.length; i++)
            {
                out.writeObject(fitResult[i]);
            }
            System.out.println("Done");
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }



    public void serve(Socket clientSocket)
    {

        ObjectOutputStream out;
        ObjectInputStream in;
        try
        {
            System.out.println("Established connection with " + clientSocket.getInetAddress());
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());

            String command = in.readUTF();
            System.out.println("Received " + command);

            if (first)
            {
                if (command.equals(INIT_STRING))
                {
                    System.out.println("Initializing server");
                    first = false;
                    init(in, out);
                }
                else
                {
                    System.out.println("Error, server not initialized");
                }
            }
            else
            {
                if (command.equals(INIT_STRING))
                {
                    System.out.println("Reinitializing server");
                    init(in, out);
                }
                else if(command.equals(DistributedEvalProtocol.EVAL_STRING))
                {
                    eval(in, out);
                }
                else if(command.equals(DistributedEvalProtocol.END_STRING))
                {
                    System.out.println("Ending connection");
                    first = true;
                }
            }
            in.close();
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void listen()
    {
        try
        {
            ServerSocket serverSocket = new ServerSocket(config.distributedPort);
            Socket clientSocket;
            while (true)
            {
                System.out.println("Waiting for client...");
                clientSocket = serverSocket.accept();
                serve(clientSocket);
                clientSocket.close();
                System.out.println("Disconnected from client");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void main(String[] args)
    {
        Config config = new Config("Config.txt");
        new DistributedEvalServer(config).listen();
    }
}
