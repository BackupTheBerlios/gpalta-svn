/*
 * PreviousOutput.java
 *
 * Created on 30 de mayo de 2005, 07:27 PM
 *
 * Copyright (C) 2005, 2006 Neven Boric <nboric@gmail.com>
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

package gpalta.nodes;

import gpalta.core.*;

/**
 * @author DSP
 */
public class PreviousOutput extends Node
{

    private int delay;

    public PreviousOutput(int delay)
    {
        this.delay = delay;
    }


    public double eval(DataHolder data, PreviousOutputHolder prev)
    {
        return prev.getData(delay);
    }

    /**
     * This should never be called. The whole evalVect was implemented to be
     * used when there are no previous outputs as Nodes
     */
    public void evalVect(double[] outVect, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        //TODO: Maybe throw an exception?
        Logger.log("Error: Should not be calling PreviousOutput's evalVect()");
    }

    public String name()
    {
        return ("Yn_" + delay);
    }

}
