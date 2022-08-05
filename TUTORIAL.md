
# GETTING STARTED WITH MM-NEAT 3.0

* MAIN WEBPAGE: https://people.southwestern.edu/~schrum2/re/mm-neat.php
* SECONDARY WEBPAGE: http://nn.cs.utexas.edu/?mm-neat
* GitHub: https://github.com/schrum2/MM-NEAT

Requirements for this code to run:

* Java 1.8
* Maven 3.5

MM-NEAT version 3.0 introduces integration with Maven, a dependency management system
that automatically downloads libraries needed for the code to function so that you do not
need to hunt down individual jar files yourself. The transition to Maven was a major move 
for this code, which is why a new GitHub repository was created to store MM-NEAT moving forward.
MM-NEAT version 2.0 does not use Maven, and will remain at https://github.com/schrum2/MM-NEATv2, 
but has not been updated since September 2017.

Also, if you want to use Generative Adversarial Networks to produce video game levels you will need:

* Python 3.7.3
* PyTorch 1.8.1
* NumPy 1.19.5
* Pillow 7.0.0

More recent versions may also work, but these are what was used to develop the code.
To quickly install the required packages, run this in command prompt or anaconda:
```
pip install -r requirements.txt
```


## INSTALLATION

1. Start by downloading and installing the latest version of Maven: https://maven.apache.org/
2. Once Maven is installed, you can clone/fork the MM-NEAT project and download it to your machine.
3. In the main project directory (MM-NEAT) use a console/terminal to execute: mvn -U install  
   Lots of text should scroll across the screen, and some graphics windows will pop up, 
   but near the end should be a result of BUILD SUCCESS

It is recommended that you use a modern IDE to work on this code. Most recent development on the
code was accomplished using Eclipse. Eclipse is integrated with Maven via the m2 plugin, so that the
project can also be built with Maven directly in Eclipse. Many other IDEs also have Maven integration.

## RUNNING

The main class of the project is 
[edu.southwestern.mmneat.MMNEAT.java](src/main/java/edu/southwestern/MMNEAT/MMNEAT.java),
but this class requires many command line parameters to function correctly.
When you execute "mvn -U install" an executable uber jar file will be created in
the "target" sub-directory: MM-NEAT-0.0.1-SNAPSHOT.jar

Once the jar file is created, the code can be launched by running 
java with the jar file and appropriate command-line arguments. 
The general form is:

```
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:<int> randomSeed:<int> base:<experiment directory> maxGens:<int> mu:<int> io:true netio:true task:<java class> log:<experiment string>-<evaluation method string> saveTo:<evaluation method directory>
```
Here is a brief description of each of these key parameters.

* runNumber:	An identifying number for the experimental run. Used in directory names and filenames.
* randomSeed:	Should generally be the same as runNumber. Specifies seed for random number generator.
* base:		Directory that will be created to store data from all experiments to be compared.
* maxGens:	Number of generations before evolution stops (alternate stop conditions can be defined).
* mu:		Population size (because (mu+lambda) evolution is assumed, mu is the number of parents).
* task:		Java class that implements the Task interface.
* log:		Prefix that will be used in all filenames. Recommended format above.
* saveTo:		Sub-directory within the base directory where data will be saved (must match portion of log name after the hyphen).

Other parameters may need to be set as well, depending on the 
type of the experiment, and whether you are ok with the default
values. All default values are listed in
[edu.southwestern.parameters.Parameters.java](src/main/java/edu/southwestern/parameters/Parameters.java).

Examples of appropriate parameters to launch experiments with
are in the many Experiment files described next.

## EXPERIMENT FILES

There are several batch files in the sub-directory "batch".
Each sub-directory with the prefix Experiments-* contains batch
files associated with experiments from either my dissertation or
from peer-reviewed publications that have used MM-NEAT since then.
In each such sub-directory there is a batch file called LAUNCHALL.bat
that you can simply double-click to launch each group of experiments
simultaneously. This is the easiest approach to recreating experimental
results, but not necessarily the best. In some cases, the number of
experiments launching may overwhelm the memory of your machine, so you
can launch the experiments individually instead. However, for the most
part these experiments were run on a condor cluster, so distributing all
execution would be best.

For more details on research conducted with MM-NEAT, see the associated list of
[publications](PUBLICATIONS.md).

After running any of these experiments, you will likely want to look at the 
results. Each experiment outputs several files with the suffix "plot" that
can be loaded in gnuplot. Gnuplot is an open-source tool for plotting
scientific data, and can be downloaded at: http://www.gnuplot.info/

Gnuplot only lets you analyze the numeric data. In order to see the evolved
behavior, and to see how modular networks use their multiple modules, there
are several batch files to analyze the results of completed experiments.

## POST EVALUATION FILES

Each batch file that starts with "post" can be used to analyze the results
of a completed experiment. Each of these batch files requires command-line
arguments in order to function properly. The specific usage of each file is
shown in comments at the top of each file. Post-evaluation files that only
apply to Ms. Pac-Man are in the MsPacMan sub-directory, but others are in 
the root MM-NEAT directory. Here is a list of what each file is used for:

* postBestEval.bat:		Evaluate champion Ms. Pac-Man net (non-visual) with original settings and save results to eval report in run directory.
* postBestFourMazeEval.bat:	Evaluate champion Ms. Pac-Man net (non-visual) with FourMaze settings and save results to eval report in run directory.
* postBestFourMazeRecord.bat:	Watch and record single eval of champion Ms. Pac-Man net with FourMaze settings.
* postBestFourMazeWatch.bat:	Watch champion Ms. Pac-Man net behavior with FourMaze settings.
* postBestMPMvsGEval.bat:		Evaluate champion Ms. Pac-Man net (non-visual) with MPMvsG settings and save results to eval report in run directory.
* postBestMPMvsGRecord.bat:	Watch and record single eval of champion Ms. Pac-Man net with MPMvsG settings.
* postBestMPMvsGWatch.bat:		Watch champion Ms. Pac-Man net behavior with MPMvsG settings.
* postBestObjectiveEval.bat:	Evaluate (non-visual) behavior of best nets in each objective with original settings.
* postBestObjectiveWatch.bat:	Watch behavior of best nets in each objective with original settings.
* postBestWatch.bat:		Watch champion Ms. Pac-Man net behavior with original settings.
* postParetoFrontWatch.bat:	Watch behavior of all members of Pareto front using original evaluation settings. Works for domains besides Ms. Pac-Man.
* postReplay.bat:			Replay any Ms. Pac-Man recording saved by one of the record batch files.

Eval reports are stored in the directory with all other data from the original 
experiment. For example, the MsPacMan-OneLifeConflict-MMD.bat file stores
data in onelifeconflict/MMD0, and the eval report created in this directory
would start OneLifeConflict-MMD0_Eval. Be carefule with eval files: Running a
batch file that creates an eval report will overwrite any existing eval report,
unless you manually change the name of any pre-existing files.

Watching, evaluating, or recording champion performance with original settings
means that the setting will be the same as in the original experiment that created
the champion. FourMaze and MPMvsG rules are specific rule sets that have been used
in the literature before. These rule sets are explained in both the dissertation
and GECCO 2014 paper referenced in [PUBLICATIONS.md](PUBLICATIONS.md).

As you watch a network's behavior, you will also see several other windows. One 
contains the evolved neural network used by the agent. Four narrow windows contain 
the sensor readings corresponding to each direction that Ms. Pac-Man checks when 
moving. Direction 0 is UP, 1 is RIGHT, 2 is DOWN, and 3 is LEFT. These windows also
show the outputs of each network for each direction checked (directions with a wall
simply have no readings). If the network has preference neurons, then the comparative
output of each preference neuron will also be displayed. Additionally, Ms. Pac-Man 
will leave trails of a difference color for each module she uses.

## TRAINING GENERATIVE ADVERSARIAL NETWORKS

Python code associated with training GANs is in a special [GAN](src/main/python/GAN)
subdirectory. Within this directory, the file [main.py](src/main/python/GAN/main.py)
is used to train GANs and the file [generator_ws.py](src/main/python/GAN/generator_ws.py)
can be used to send latent vectors to a pretrained GAN and retrieve the resulting
output. Here is an example command for training a GAN using data from the game Lode Runner:

```
python main.py --niter 5000 --nz 10 --json LodeRunnerAllTilesLevel1to10.json --experiment LodeRunnerAllTilesLevel1to10-5000_10_7 --tiles 7 --cuda
```

The meaning of the command line parameters is explained within main.py. However, the thing that is needed most
is a json file containing training samples representing 2D game levels. The existing json training sets can be
viewed to understand the format of the data. There are several evolution tasks defined using Java code that
send latent vectors to generator_ws.py and take the output to produce game levels. One example is [MarioGANLevelTask](src/main/java/edu/southwestern/tasks/mario/MarioGANLevelTask.java).

## OTHER DOMAINS

This code was initially developed to evolve Ms. Pac-Man agents, but other domains
are also integrated with the code. Not all domains are fully developed, but much work
has been done since the original work in Ms. Pac-Man.

### RL Glue

First, the code is integrated with RL-Glue, which is a system for evaluating
Reinforcement Learning algorithms available here:

http://glue.rl-community.org

You don't need to download any additional files for RL-Glue to work, but the webpage
may provide assistance in understanding how it works. RL-Glue domains
need to be tweaked slightly to work with MM-NEAT, but a few are already functioning:
PuddleWorld, CartPole, MountainCar, and Tetris. Visualization can be viewed if "watch:true" 
is included in the command line. Post visualizations with some of the batch files above
also work (postParetoFrontWatch.bat and postBestObjective*). Most of these domains
have not received much attention, but Tetris is an exception 
(see [PUBLICATIONS.md](PUBLICATIONS.md)). 

Here is a quick test of MM-NEAT using a simple RL Glue domain: the cartpole domain,
also known as pole balancing or the inverted pendulum problem. First, navigate to the "batch\RLGlue-Other"
sub-directory in a terminal/console and run the following command:

```
RLGlue-CartPole-Markovian.bat 0
```

The commandline parameter 0 is an experiment id number that can be swapped with another number.
Note that these are batch files intended for use in Windows. However, they can be esily converted into
bash files by replacing each occurrence of a % in RLGlue-CartPole-Markovian.bat with a $. The resulting file
could then be executed as a bash file on Mac/Linux/Unix.

Once this brief experiment finishes, you can view the final behavior by running the following command 
from the root MM-NEAT directory:

```
postAllBestObjectiveWatch.bat cartpole RL Markovian 0 5
```

The command line parameter "cartpole" refers to the base sub-directory where results are stored.
The "RL" parameter is a prefix in the file name of the log file for the experiment that is within a
sub-directory "Markovian0". Note that the two parts of this name are specified by separate parameters:
"Markovian" and "0". Finally, the parameter "5" indicates how many trials of the evolved champion to watch.

### Breve Monsters

Another domain included with this code is a 2D reimplementation of the BREVE Monsters
domain, in which many of the methods used in MM-NEAT (Module Mutation, TUG) were first
tested. The original BREVE Monsters can be downloaded at:

http://nn.cs.utexas.edu/?brevemonsters

The BREVE 2D domain in MM-NEAT is not identical to BREVE Monsters, but it is similar in 
many ways. The specific domains implemented are Front-Back Ramming (FBR),
Predator/Prey (PP), and Fight or Flight (FoF). Some work is still needed to bring this
code up to the level of the original BREVE Monsters experiments, but you are free to play
with it. Some known issues: monitoring the inputs to the networks causes strange color
clashes, likely because each monster has its own network and is trying to write data to
the same window simultaneously; the sensors don't all work the same as they do in BREVE;
the camera does not follow the agents, so sometimes all the action happens out of sight;
FoF features a ramming orb instead of a bat as in the original BREVE implementation;
and for some reason the dynamics of the FoF don't allow baiting behavior to emerge (yet). 
Still, the code is a useful starting point. Some batch files to get you started are in batch/Breve2D/.

The current implementation of these domains produces some different results from those
in the original BREVE Monsters domain. If you manage to produce any interesting results
with this code, or fix any of the issues mentioned above, please let me know by emailing
me at schrum2@southwestern.edu

### More Domains

Other pre-existing domains include a toroidal Predator/Prey grid world (see [PUBLICATIONS.md](PUBLICATIONS.md)), 
several board games, Super Mario, and more. Several ways of interactively creating interesting 
art and sounds are also available (see [INTERACTIVE_EVOLUTION.md](INTERACTIVE_EVOLUTION.md)). 
Please explore!

### Python

Although most code is in Java, some domains make limited use of Python scripts. Specifically, Python 3.7. 
In order to make use of these domains, 
you must create a text file called ``my_python_path.txt`` in the root MM-NEAT directory that contains the path to your
python executable. If you only have one version of Python on your machine, and it is part of your PATH environment variable,
then you might be able to simply put ``python`` into this text file. However, a full path is recommended.

Some code also makes use of PyTorch 1.0.

### Minecraft

The Minecraft domain focuses on evolving shapes using the [EvoCraft API](https://github.com/real-itu/Evocraft-py) which also requires
Python as described above. There are several batch files that can be run to generate a myriad of different shapes, however, doing this requires
additional set up. You'll need a copy of Minecraft Java edition on your machine, and you'll need to get a modded EvoCraft server running.
The instructions for this can be found here:

[Minecraft Instructions](Minecraft.md)

All of the shapes are generated in the world, around coordinates 0,5,0. Instructions on teleporting there, as well as other specifics are
also in the linked file, but it's important to note that teleporting yourself below the y coordinate of 5 can cause issues, sometimes
even teleporting you under the world. Another thing to keep in mind is that some of the batch files may take a few minutes to initialize
before anything is placed in the world, as they need to clear out the space in the world before placing the shapes. 
Instructions on running the batch files are also in the file linked above.

## MAKING YOUR OWN DOMAINS

All of the pre-existing domains should provide useful examples of how to integrate a new
domain either of your own design, or made by someone else. Here are some general hints
on how to do this.

1. Make your domain implement the Task interface. Specifically, you may want to extend one
   of the classes in the [edu.southwestern.tasks](src/main/java/edu/southwestern/tasks) 
   package that already implements the 
   [Task interface](src/main/java/edu/southwestern/tasks/Task.java). 
   [LonerTask](src/main/java/edu/southwestern/tasks/LonerTask.java) 
   is appropriate for any task where only a single genotype is evaluated
   at a time. However, if the domain has noisy evaluations, then 
   [NoisyLonerTask](src/main/java/edu/southwestern/tasks/NoisyLonerTask.java) 
   is more appropriate.
2. You may need to set up additional parameters related to your domain in
   [edu.southwestern.parameters.Parameters.java](src/main/java/edu/southwestern/parameters/Parameters.java)
3. You may need to edit 
   [edu.southwestern.mmneat.MMNEAT.java](src/main/java/edu/southwestern/MMNEAT/MMNEAT.java)
   so that when the game task
   matches your new task, it prepares evolution to run your domain (for example, by 
   instantiating classes or setting up parameters unique to your domain).

If you integrate any interesting domains into MM-NEAT, I would love to hear about it
by email at schrum2@southwestern.edu. You can also create an Issue or pull request directly in GitHub.
Feel free to contact me for help with integrating your domains as well. 
