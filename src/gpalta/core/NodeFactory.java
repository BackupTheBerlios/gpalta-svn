/*
 * NodeTypesConfig2.java
 *
 * Created on 29 de marzo de 2006, 12:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gpalta.core;

import gpalta.nodes.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 *
 * @author DSP
 */
public class NodeFactory
{
    private Config config;
    private NodeSet[] nodeSets;
    public NodeSet treeRoot;
    
    public NodeFactory(Config config, DataHolder data)
    {
        this.config = config;
        try
        {
            FileInputStream in = new FileInputStream(config.nodeConfigFileName);
            Properties props = new Properties();
            props.load(in);
            in.close();
            
            String separator = "\\s*,\\s*";
            
            String tmp = props.getProperty("sets");
            String[] sets = tmp.split(separator);
            
            nodeSets = new NodeSet[sets.length];
            
            int i,j,k;
            for (i=0; i<sets.length; i++)
            {
                nodeSets[i] = new NodeSet(sets[i]);
                tmp = props.getProperty(sets[i] + "Functions");
                String[] nodes = tmp.split(separator);
                for (j=0; j<nodes.length; j++)
                {
                    Class cl = Class.forName("gpalta.nodes." + nodes[j]);
                    java.lang.reflect.Constructor co = cl.getConstructor();
                    nodeSets[i].functions.add((Node)co.newInstance());
                }
                tmp = props.getProperty(sets[i] + "Terminals");
                nodes = tmp.split(separator);
                for (j=0; j<nodes.length; j++)
                {
                    Class cl = Class.forName("gpalta.nodes." + nodes[j]);
                    if (nodes[j].contains("Var"))
                    {
                        for (k=0; k<data.nVars; k++)
                        {
                            java.lang.reflect.Constructor[] co = cl.getConstructors();
                            nodeSets[i].terminals.add((Node)co[0].newInstance(k+1));
                        } 
                    }
                    else
                    {
                        java.lang.reflect.Constructor co = cl.getConstructor();
                        nodeSets[i].terminals.add((Node)co.newInstance());
                    }
                }
                nodeSets[i].all.addAll(nodeSets[i].functions);
                nodeSets[i].all.addAll(nodeSets[i].terminals);
            }
            
            for (i=0; i<nodeSets.length; i++)
            {
                for (Node n:nodeSets[i].all)
                {
                    tmp = props.getProperty("kids" + n.getClass().getSimpleName());
                    if (tmp != null)
                    {
                        String[] kids = tmp.split(separator);
                        if (kids.length != n.nKids())
                        {
                            Logger.log("Error reading " + config.nodeConfigFileName + ": must specify " + n.nKids() + " kids for " + n.getClass().getSimpleName());
                        }
                        for (j=0; j<n.nKids(); j++)
                        {
                            for (k=0; k<nodeSets.length; k++)
                            {
                                if (nodeSets[k].name.equals(kids[j]))
                                {
                                    n.setTypeOfKids(j, nodeSets[k]);
                                    break;
                                }
                            }
                            if (k == nodeSets.length)
                                Logger.log("Error reading " + config.nodeConfigFileName + ": " + kids[j] + " doesn't match any set name");
                        }
                    }
                    else
                        for (j=0; j<n.nKids(); j++)
                            n.setTypeOfKids(j, nodeSets[i]);
                }
            }
            tmp = props.getProperty("treeRoot");
            if (tmp == null)
            {
                Logger.log("Error reading " + config.nodeConfigFileName + ": property \"treeRoot\" not present");
            }
            for (i=0; i<nodeSets.length; i++)
            {
                if (nodeSets[i].name.equals(tmp))
                    treeRoot = nodeSets[i];
            }
            if (i == nodeSets.length)
                Logger.log("Error reading " + config.nodeConfigFileName + ": " + tmp + " doesn't match any set name");
        }
        catch (IOException e)
        {
            Logger.log("Error reading " + config.nodeConfigFileName + ":");
            Logger.log(e);
        }
        catch (ClassNotFoundException e)
        {
            Logger.log("Error reading " + config.nodeConfigFileName + ":");
            Logger.log(e);
        }
        catch (NoSuchMethodException e)
        {
            Logger.log("Error reading " + config.nodeConfigFileName + ":");
            Logger.log(e);
        }
        catch (InstantiationException e)
        {
            Logger.log("Error reading " + config.nodeConfigFileName + ":");
            Logger.log(e);
        }
        catch (IllegalAccessException e)
        {
            Logger.log("Error reading " + config.nodeConfigFileName + ":");
            Logger.log(e);
        }
        catch (InvocationTargetException e)
        {
            Logger.log("Error reading " + config.nodeConfigFileName + ":");
            Logger.log(e);
        }
    }
    
    /**
     * Obtain a new Node randomly chosen from the given list.
     * The Node is cloned and initialized, so it can be used separatedly
     *
     * @param l The list of Nodes from which to choose
     * @param currentGlobalDepth The depth of the requested Node in the Tree
     */
    public Node newRandomNode(List<Node> l, int currentGlobalDepth)
    {
        int which = Common.globalRandom.nextInt(l.size());
        Node outNode = null;
        try
        {
            outNode = (Node)l.get(which).clone();
        }
        /* This should never happen, as nodes do support cloning, so it's ok
         * to catch this exception here
         */
        catch (CloneNotSupportedException e)
        {
            Logger.log(e);
        }
        outNode.init(config);
        outNode.currentDepth = currentGlobalDepth;     
        return outNode;
    }
    
    public static boolean isInList(Node node, List<Node> l)
    {
        for (Node n : l)
        {
            if (n.getClass() == node.getClass())
                return true;
        }
        return false;
    }
    
    public Node newNode(String name, int currentGlobalDepth) 
    {
        System.out.println(name);
        Node outNode = null;
        if (name.startsWith("X"))
        {
            outNode = new RealVar(Integer.parseInt(name.substring(1)));
        }
        else if (name.substring(0,1).matches("\\d"))
        {
            outNode = new RealConstant(Double.parseDouble(name));
        }
        else if (name.equals("true"))
        {
            outNode = new LogicConstant(1);
        }
        else if (name.equals("false"))
        {
            outNode = new LogicConstant(0);
        }
        else
        {
            List<Node> all = new ArrayList<Node>();
            for (int i=0; i<nodeSets.length; i++)
            {
                all.addAll(nodeSets[i].all);
            }
            for (Node n : all)
            {
                if (name.equals(n.name()))
                {
                    try
                    {
                        outNode = (Node)n.clone();
                    }
                    catch (CloneNotSupportedException e)
                    {
                        Logger.log(e);
                    }
                    break;
                }
            }
        }
        outNode.currentDepth = currentGlobalDepth;
        return outNode;
    }
    
}
