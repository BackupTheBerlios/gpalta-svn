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


Configuraton:

Edit Config.txt to change GP parameters
Edit NodeConfig.txt to change available nodes and how they connect to each other

For more details, see:
http://gpalta.berlios.de

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

GPalta uses JChart2d for plotting inside the graphical interface.
JChart2D is a minimalistic charting library published under the LGPL.
For more details, see the project web page:
http://jchart2d.sourceforge.net



