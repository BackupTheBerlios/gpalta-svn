/*
 * EvalVectors.java
 *
 * Created on 11 de noviembre de 2005, 04:35 PM
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

package gpalta.core;

import java.util.*;

/**
 *
 * @author neven
 */
public class EvalVectors {
    
    private List<double[]> vectors;
    private int currentEvalVector;
    private int vectorSize;
    
    /** Creates a new instance of EvalVectors */
    public EvalVectors(int vectorSize) 
    {
        this.vectorSize = vectorSize;
        vectors = new ArrayList<double[]>(0);
        currentEvalVector = -1;
    }

    public synchronized double[] get()
    {
        currentEvalVector++;
        if (currentEvalVector == vectors.size())
        {
            //Logger.log("Adding new realEvalVector, " + currentRealEvalVector);
            vectors.add(new double[vectorSize]);
        }
        return vectors.get(currentEvalVector);
    }
            
    public synchronized void release()
    {
        currentEvalVector--;
    }
    
}
