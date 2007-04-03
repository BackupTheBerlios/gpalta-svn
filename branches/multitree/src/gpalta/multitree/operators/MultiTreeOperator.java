/*
 * MultiTreeOperator.java
 *
 * Created on 04-01-2007, 01:38:40 AM
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

package gpalta.multitree.operators;

import gpalta.core.*;
import gpalta.ops.TreeOperator;
import gpalta.multitree.MultiTreeIndividual;

import java.util.List;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class MultiTreeOperator
{
    protected int nOperators;
    public LowLevelMultiTreeOperator[] lowLevelOps;
    double[] operatorUpperProbs;

    public MultiTreeOperator(Config config, TreeOperator treeOp)
    {
        try
        {
            FileInputStream in = new FileInputStream(config.operatorConfigFileName);
            Properties props = new Properties();
            props.load(in);
            in.close();

            nOperators = Integer.parseInt(props.getProperty("nOperators"));
            lowLevelOps = new LowLevelMultiTreeOperator[nOperators];
            operatorUpperProbs = new double[nOperators];
            boolean useEqProb = Boolean.parseBoolean(props.getProperty("useEqProb"));
            for (int i=0; i< nOperators; i++)
            {
                Class cl = Class.forName(props.getProperty("op" + (i+1)));
                java.lang.reflect.Constructor[] co = cl.getConstructors();
                if (co.length != 1)
                {

                }
                else
                {
                    Class[] params = co[0].getParameterTypes();
                    if (params.length == 1 && params[0].getName().equals(TreeOperator.class.getName()))
                    {
                        lowLevelOps[i] = (LowLevelMultiTreeOperator) co[0].newInstance(treeOp);
                    }
                    else
                    {

                    }
                }
                if (useEqProb)
                    operatorUpperProbs[i] = 1./nOperators*(1 + i++);
                else
                    operatorUpperProbs[i] = Double.parseDouble(props.getProperty("op" + (i+1) + "UpProb"));
            }
        }
        catch (IOException e)
        {
            Logger.log("Error reading " + config.operatorConfigFileName + ":");
            Logger.log(e);
        }
        catch (IllegalAccessException e)
        {
            Logger.log("Error reading " + config.operatorConfigFileName + ":");
            Logger.log(e);
        }
        catch (InvocationTargetException e)
        {
            Logger.log("Error reading " + config.operatorConfigFileName + ":");
            Logger.log(e);
        }
        catch (InstantiationException e)
        {
            Logger.log("Error reading " + config.operatorConfigFileName + ":");
            Logger.log(e);
        }
        catch (ClassNotFoundException e)
        {
            Logger.log("Error reading " + config.operatorConfigFileName + ":");
            Logger.log(e);
        }
    }


    public <M extends MultiTreeIndividual> LowLevelMultiTreeOperator[] operate(List<M> population, TempVectorFactory tempVectorFactory, ProblemData problemData)
    {
        LowLevelMultiTreeOperator[] operatorsApplied = new LowLevelMultiTreeOperator[population.size()];
        double op;
        for (int i = 0; i < population.size(); i++)
        {
            op = Common.globalRandom.nextDouble();
            for (int j=0; j<nOperators; j++)
            {
                if (op <= operatorUpperProbs[j])
                {
                    LowLevelMultiTreeOperator operator = lowLevelOps[j];
                    if (i != population.size() - operator.nIndividuals() + 1)
                    {
                        MultiTreeIndividual[] inds = new MultiTreeIndividual[operator.nIndividuals()];
                        inds[0] = population.get(i);
                        operatorsApplied[i] = operator;
                        for (int k=1; k<inds.length; k++)
                        {
                            inds[k] = population.get(i+k);
                            operatorsApplied[i+k] = operator;
                        }
                        if (operator.operate(inds, tempVectorFactory, problemData))
                        {
                            inds[0].fitCalculated = false;
                            for (int k=1; k<inds.length; k++)
                            {
                                inds[k].fitCalculated = false;
                                i++;
                            }
                        }
                        break;
                    }
                }
            }
        }
        return operatorsApplied;
    }

//    public <M extends MultiTreeIndividual> void operate(List<M> population)
//    {
//        int[] perm = Common.randPerm(population.size());
//        double op;
//        for (int i = 0; i < population.size(); i++)
//        {
//            op = Common.globalRandom.nextDouble();
//            if (op <= config.upLimitProbCrossOver)
//            {
//                //Do cross over, except for the last tree:
//                if (i != population.size() - 1)
//                {
//                    int t1, t2;
//                    double probSelectWorst = Common.globalRandom.nextDouble();
//                    if (probSelectWorst < 0.5)
//                    {
//                        double minV = population.get(perm[i]).getTree(0).readFitness();
//                        t1 = 0;
//                        for (int j=1; j<population.get(perm[i]).nTrees(); j++)
//                        {
//                            if (population.get(perm[i]).getTree(j).readFitness() < minV)
//                            {
//                                minV = population.get(perm[i]).getTree(j).readFitness();
//                                t1 = j;
//                            }
//                        }
//                        minV = population.get(perm[i+1]).getTree(0).readFitness();
//                        t2 = 0;
//                        for (int j=1; j<population.get(perm[i+1]).nTrees(); j++)
//                        {
//                            if (population.get(perm[i+1]).getTree(j).readFitness() < minV)
//                            {
//                                minV = population.get(perm[i+1]).getTree(j).readFitness();
//                                t2 = j;
//                            }
//                        }
//                    }
//                    else
//                    {
//                        t1 = Common.globalRandom.nextInt(population.get(perm[i]).nTrees());
//                        t2 = Common.globalRandom.nextInt(population.get(perm[i+1]).nTrees());
//                    }
//                    if (treeOp.crossOver(population.get(perm[i]).getTree(t1), population.get(perm[i+1]).getTree(t2)))
//                    {
//                        population.get(perm[i]).fitCalculated = false;
//                        population.get(perm[i + 1]).fitCalculated = false;
//                        i++;
//                    }
//                }
//            }
//            else if (op <= config.upLimitProbMutation)
//            {
//                int t1;
//                double probSelectWorst = Common.globalRandom.nextDouble();
//                if (probSelectWorst < 0.5)
//                {
//                    double minV = population.get(perm[i]).getTree(0).readFitness();
//                    t1 = 0;
//                    for (int j=1; j<population.get(perm[i]).nTrees(); j++)
//                    {
//                        if (population.get(perm[i]).getTree(j).readFitness() < minV)
//                        {
//                            minV = population.get(perm[i]).getTree(j).readFitness();
//                            t1 = j;
//                        }
//                    }
//                }
//                else
//                {
//                    t1 = Common.globalRandom.nextInt(population.get(perm[i]).nTrees());
//                }
//                population.get(perm[i]).fitCalculated = false;
//                treeOp.mutateBuild(population.get(perm[i]).getTree(t1));
//            }
//            else
//            {
//                //Do nothing (reproduction)
//            }
//        }
//    }

}
