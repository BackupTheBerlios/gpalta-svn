This is GPalta, a Genetic Programming Toolbox written in Java

To start the graphical evolution process, use run.bat on windows or
run.sh on unix/linux

GPalta requires java version 1.5 or later (J2SE 5.0 as Sun likes to call it)

Fitness cases must be provided this way:

data.txt, inputs:

x1(1) x2(1)...
x1(2) x2(2)...
x1(3) x2(3)...
...
(every row is a sample and every column is an input variable)

class.txt, desired outputs:

y(1)
y(2)
y(3)
...

The included data.txt and class.txt files contain random samples with
the form:

y = x2*x1^2 + x2*x1 + x2

GPalta should solve this problem easily

See the console (or log.txt file) for debugging output if any errors occur

For more details, see:
http://hayabusa.die.uchile.cl/~nboric/gpalta

If that page is unavailable, you can contact the author by e-mail:

Neven Boric <nboric@gmail.com>



Legal Notice:

GPalta is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

GPalta is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with GPalta; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA


Important notice:

GPalta is currently distributed including a copy of PtPlot, a plotting library
from the Ptolemy system:
http://ptolemy.eecs.berkeley.edu/java/ptplot/index.htm

It is my understanding that this inclusion is in no violation of either the GPL
license under wich GPalta is distributed, or the copyright agreement of PtPlot,
stated below:

Copyright agreement for PtPlot

Copyright (c) 1995-2005 The Regents of the
University of California.  All rights reserved.

Permission is hereby granted, without written
agreement and without license or royalty fees, to
use, copy, modify, and distribute this software
and its documentation for any purpose, provided
that the above copyright notice and the following
two paragraphs appear in all copies of this
software.

IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE
LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL,
INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION,
EVEN IF THE UNIVERSITY OF CALIFORNIA HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

THE UNIVERSITY OF CALIFORNIA SPECIFICALLY
DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS ON AN
"AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA
HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
