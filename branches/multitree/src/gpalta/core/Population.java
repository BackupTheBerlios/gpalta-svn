/*
 * Population.java
 *
 * Created on 31 de mayo de 2006, 03:30 PM
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

import gpalta.ops.*;

/**
 * Holds the Individuals and performs the most important operations on them: interpreting their
 * output, and organizing them for selection and for evolution.
 *
 * @author neven
 */
public interface Population
{

    public void eval(Fitness f, TempVectorFactory tempVectorFactory, DataHolder data);

    /**
     * Get the output directly from the Individual, without further processing
     *
     * @return A totally independent Output object
     */
    public Output getRawOutput(Individual ind, TempVectorFactory tempVectorFactory, DataHolder data);

    public Output getProcessedOutput(Individual ind, Fitness f, TempVectorFactory tempVectorFactory, DataHolder data);

    /**
     * Get a certain individual
     * @param which The individual to get
     */
    public Individual get(int which);

    public void init(Config config, DataHolder data, TreeBuilder builder);

    public void doSelection(IndSelector sel);

    public void evolve(TreeOperator op);

}
