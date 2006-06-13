/*
 * Individual.java
 *
 * Created on 31 de mayo de 2006, 03:48 PM
 *
 * Copyright (C) 2006 Neven Boric <nboric@gmail.com>
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

package gpalta.core;

/**
 *
 * @author neven
 */
public abstract class Individual
{
    private double fitness;
    private boolean isOnPop;
    
    public abstract int getSize();
    public abstract Individual deepClone();
    
    public double readFitness()
    {
        return fitness;
    }

    public void setFitness(double fit)
    {
        fitness = fit;
    }
    
    public void setOnPop(boolean flag)
    {
        isOnPop = flag;
    }

    public boolean isOnPop()
    {
        return isOnPop;
    }
    
}
