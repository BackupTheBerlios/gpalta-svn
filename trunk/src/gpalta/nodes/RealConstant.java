/*
 * RealConstant.java
 *
 * Created on 11 de mayo de 2005, 12:17 AM
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
 *
 * @author neven
 */
public class RealConstant extends Node
{
    
    private double constant;
    
    public RealConstant()
    {
        
    }
    
    public RealConstant(double constant) 
    {
        this.constant = constant;
    }
    
    public double eval(DataHolder data, PreviousOutputHolder prev)
    {
        return ( constant );
    }
    
    public void evalVect(double[] outVect, EvalVectors evalVectors, DataHolder data, PreviousOutputHolder prev)
    {
        for (int i=0; i < data.nSamples; i++)
        {
            outVect[i] = constant;
        }
    }
    
    public String name()
    {
        return ("" + constant);
    }
    
    public void init(Config config)
    {
        double random01 = Common.globalRandom.nextDouble();
        this.constant = config.constLowLimit + (config.constUpLimit-config.constLowLimit)*random01;
    }
}
