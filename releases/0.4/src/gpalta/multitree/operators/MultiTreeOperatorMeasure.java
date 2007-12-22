/*
 * MultiTreeOperatorMeasure.java
 *
 * Created on 21-03-2007, 04:21:00 PM
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
import java.util.ArrayList;

public class MultiTreeOperatorMeasure extends MultiTreeOperator
{

    public MultiTreeOperatorMeasure(Config config, TreeOperator treeOp)
    {
        super(config , treeOp);
    }


    public LowLevelMultiTreeOperator[] operate(List<MultiTreeIndividual> population, TempVectorFactory tempVectorFactory, ProblemData problemData)
    {
        LowLevelMultiTreeOperator[] operatorsApplied = new LowLevelMultiTreeOperator[population.size()];

        int maxInd = 0;
        for (LowLevelMultiTreeOperator operator : lowLevelOps)
            maxInd = Math.max(maxInd, operator.nIndividuals());
        List<MultiTreeIndividual> firstIndividuals = new ArrayList<MultiTreeIndividual>();
        for (int i = 0; i < maxInd; i++)
        {
            firstIndividuals.add(population.get(i));
        }

        int nDone = 0;
        for (LowLevelMultiTreeOperator operator : lowLevelOps)
        {
            MultiTreeIndividual[] inds = new MultiTreeIndividual[operator.nIndividuals()];
            for (int i = 0; i < operator.nIndividuals(); i++)
            {
                inds[i] = (MultiTreeIndividual) firstIndividuals.get(i).deepClone();
            }
            if (operator.operate(inds, tempVectorFactory, problemData))
            {
                for (int i = 0; i < inds.length; i++)
                {
                    inds[i].fitCalculated = false;
                }
            }
            for (int i = 0; i < operator.nIndividuals(); i++)
            {
                population.set(nDone + i, inds[i]);
                operatorsApplied[nDone + i] = operator;
            }
            nDone += operator.nIndividuals();
        }

        double op;
        for (int i = nDone; i < population.size(); i++)
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
                        //operatorsApplied[i] = operator;
                        for (int k=1; k<inds.length; k++)
                        {
                            inds[k] = population.get(i+k);
                            //operatorsApplied[i+k] = operator;
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

}
