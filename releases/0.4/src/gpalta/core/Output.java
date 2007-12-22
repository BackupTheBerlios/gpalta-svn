/*
 * Output.java
 *
 * Created on 31 de mayo de 2006, 11:09 PM
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

import java.io.Serializable;

/**
 * Stores the outputs of an individual. It can be used to represent multi-dimensional outputs
 *
 * @author neven
 */
public abstract class Output implements Serializable, Cloneable
{

    /**
     * Get the dimension (number of scalar outputs per sample) of this Output
     */
    public abstract int getDim();

    /**
     * Must implement clone() so Outputs can be cloned witout knowing the actual subclass.
     * Subclasses must override this to make sure that cloned instances are independent.
     * @return
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

}
