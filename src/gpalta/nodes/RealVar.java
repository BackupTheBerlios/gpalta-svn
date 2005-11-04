/*
 * RealVar.java
 *
 * Created on 12 de mayo de 2005, 01:24 AM
 *
 * Copyright (C) 2005  Neven Boric <nboric@gmail.com>
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
import java.util.*;
import gpalta.core.*;

/**
 * Real variable terminal
 *
 * @author neven
 */
public class RealVar extends Node
{
    public int whichVar;
    
    /** Creates a new instance of RealVar */
    public RealVar(int whichVar) 
    {
        this.whichVar = whichVar;
    }
    
    public double eval(Evolution evo)
    {
        return evo.dataHolder.getData(whichVar);
    }
    
    public void evalVect(Evolution evo, double[] outVect)
    {
        System.arraycopy(evo.dataHolder.getDataVect(whichVar), 0, outVect, 0, evo.dataHolder.nSamples);
    }
    
    public String name()
    {
        return ("X" + whichVar);
    }

}