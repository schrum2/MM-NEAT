# MM-NEAT 3.0

To learn how to compile/use, skip straight to [TUTORIAL.md](https://github.com/schrum2/MM-NEAT/blob/master/TUTORIAL.md)

Copyright (c) 2014 The University of Texas at Austin 
and 2016 Southwestern University.
All rights reserved.
Refer to [LICENSE.txt](https://github.com/schrum2/MM-NEAT/blob/master/LICENSE.txt) 
for detailed license information.
Also see [copyright.txt](https://github.com/schrum2/MM-NEAT/blob/master/copyright.txt) 
for copyright information about the
included Ms. Pac-Man code.

* MAIN WEBPAGE: https://people.southwestern.edu/~schrum2/re/mm-neat.php
* SECONDARY WEBPAGE: http://nn.cs.utexas.edu/?mm-neat

## ABOUT

MM-NEAT stands for Multiobjective Modular Neuro-Evolution of Augmenting Topologies.
It is inspired by the original NEAT, but also incorporates multiobjective evolution
via NSGA-II, and supports several forms of modular neural networks. Support for
the fitness shaping technique Targeting Unachieved Goals (TUG) is also included.
Indirect encoding via HyperNEAT is also supported, as is interactive evolution of
several types of interesting artifacts (pictures, sounds, animations) using CPPNs. 
Furthermore, the code can be used to train GANs to emulate tile-based video game
levels, and then evolve latent vectors that find levels with desireable properties.
The code was originally developed by Jacob Schrum (schrum2@southwestern.edu) while 
at the University of Texas at Austin, but has since been improved upon by several
undergraduate students at Southwestern University in Georgetown, TX, where
Dr. Schrum is currently a professor in the department of Math and Computer Science.
Links to publications and demos further explaining the code are available at 
Dr. Schrum's personal webpage:

http://www.southwestern.edu/~schrum2/

A list of publications is also in [PUBLICATIONS.md](https://github.com/schrum2/MM-NEAT/blob/master/PUBLICATIONS.md).

More information on NEAT is available in:

K. O. Stanley and R. Miikkulainen, "Evolving Neural Networks Through 
Augmenting Topologies." Evolutionary Computation, 10(2):99-127, 2002.
URL: http://nn.cs.utexas.edu/keyword?stanley:ec02

More information on HyperNEAT is available in:

K. O. Stanley, D. B. D'Ambrosio, and J. Gauci, "A Hypercube-Based 
Encoding for Evolving Large-Scale Neural Networks." Artificial Life, 
15(2):185–212, 2009.
URL: http://eplex.cs.ucf.edu/publications/2009/stanley-alife09

Information on NSGA-II is available in:

K. Deb, S. Agrawal, A. Pratap, and T. Meyarivan, "A Fast Elitist Non-dominated
Sorting Genetic Algorithm for Multi-Objective Optimization: NSGA-II". 
Parallel Problem Solving from Nature (PPSN VI), pp. 849-858, 2000.
URL: https://doi.org/10.1007/3-540-45356-3_83

A precursor to MM-NEAT is the BREVE Monsters software package, available at:

http://nn.cs.utexas.edu/?brevemonsters

MM-NEAT was developed primarily to evolve multimodal behavior in Ms. Pac-Man,
and therefore includes (modified) code for the Ms. Pac-Man simulator created for
the Ms. Pac-Man vs. Ghosts Competitions. The original version of this code does not
seem to be available anymore, but a newer version associated with the latest competition
can be downloaded at:

http://www.pacmanvghosts.co.uk/

For further instructions on how to run this code, see [TUTORIAL.md](https://github.com/schrum2/MM-NEAT/blob/master/TUTORIAL.md).

For information on the different types of interactive evolution tasks in the code,
see [INTERACTIVE_EVOLUTION.md](https://github.com/schrum2/MM-NEAT/blob/master/INTERACTIVE_EVOLUTION.md).

FOR MORE INFORMATION CONTACT

schrum2@southwestern.edu

