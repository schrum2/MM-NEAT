# INTERACTIVE EVOLUTION WITH MM-NEAT

For a simplified version of MM-NEAT that only includes the code for interactive evolution using
CPPNs, go to the [CPPNArtEvolution Project](https://github.com/schrum2/CPPNArtEvolution).

Within the MM-NEAT code repository, there are several programs that are based on interactive evolution. All of
these classes extend the same general Interactive Evolution Task, which launches an applet displaying 20 items
that can be evolved, saved, and manipulated by the user over unlimited generations. InteractiveEvolutionTask
uses CPPNs (Compositional Pattern Producing Networks) to mimic the process of natural evolution, utilizing 
various activation functions to create repetition, variation, and symmetry throughout the generated items. 
There are buttons across the top of the interface that allow the user to reset, undo, evolve, save, and show
the underlying networks of selected sounds/shapes/images/animations. The CPPN inputs can also be turned on and off 
through checkboxes, and there are various activation functions to manipulate the items in interesting ways 
that can be turned on and off through checkboxes. 

## [Picbreeder](https://github.com/schrum2/MM-NEAT/blob/master/src/main/java/edu/southwestern/tasks/interactive/picbreeder/PicbreederTask.java)

Picbreeder evolves original images by taking in an x-coordinate, a y-coordinate, the distance from the 
center, and a bias of 1.0 as the inputs to the CPPN, and outputting hue, saturation, and brightness values 
that are used to plot the color at the input (x,y). This process is repeated for each coordinate in the generated 
image, so that the images have incredibly diverse and colorful patterns.

* Based on previous work: 
  Jimmy Secretan, Nicholas Beato, David B. D'Ambrosio, Adelein Rodriguez, Adam Campbell, Jeremiah T. Folsom-Kovarik and Kenneth O. Stanley. Picbreeder: A Case Study in Collaborative Evolutionary Exploration of Design Space. Evolutionary Computation 19, 3 (2011), 373–403. DOI: http://dx.doi.org/10.1162/evco_a_00030

## [Breedesizer](https://github.com/schrum2/MM-NEAT/blob/master/src/main/java/edu/southwestern/tasks/interactive/breedesizer/BreedesizerTask.java)

Breedesizer evolves original sounds by taking in a time input, the sine of time input, and a bias of 1.0 and 
outputting an amplitude wave that can be played back as a sound. The user can play back any selected sound or 
sounds with a selected MIDI file, using the created sound as the "instrument". The Breedesizer also launches a
keyboard applet that can be used to play the generated sound at different frequencies (or pitches).

* Based on previous work: 
  Björn Þór Jónsson, Amy K. Hoover, and Sebastian Risi. Interactively Evolving Compositional Sound Synthesis Networks. Proceedings of the 2015 Genetic and Evolutionary Computation Conference (GECCO '15). Pages 321-328. 2015. DOI: http://dx.doi.org/10.1145/2739480.2754796

## [ThreeDimensionalObjectBreeder](https://github.com/schrum2/MM-NEAT/blob/master/src/main/java/edu/southwestern/tasks/interactive/objectbreeder/ThreeDimensionalObjectBreederTask.java)

ThreeDimensionalObjectBreeder evolves original 3D objects that can be animated to rotate or rotated manually 
through sliders by taking in an x-coordinate, a y-coordinate, a z-coordinate, a distance from the center, and 
a bias of 1.0 as inputs to the CPPN, and outputting whether there is a cube present and hue, saturation, and 
brightness values that are used to construct a cube of a specific color if the threshold is met for a cube to 
be present. 

* Based on previous work: 
  Jeff Clune and Hod Lipson. 2011. Evolving 3D objects with a generative encoding inspired by developmental biology. Proceedings of the European Conference on Artificial Life. 144-148. URL: http://www.evolvingai.org/files/2011-CluneLipson-Evolving3DObjectsWithCPPNs-ECAL.pdf

## [AnimationBreeder](https://github.com/schrum2/MM-NEAT/blob/master/src/main/java/edu/southwestern/tasks/interactive/animationbreeder/AnimationBreederTask.java)

AnimationBreeder evolves original animations by taking in an x-coordinate, a y-coordinate, the distance from the 
center, time, and a bias of 1.0 as the inputs to the CPPN, and outputting hue, saturation, and brightness 
values that are used to plot the color at the input (x,y) at a given time. The class uses the same setup as 
Picbreeder, but the addition of a time input allows the images to vary over time, creating an animated pattern.

* Developed using MM-NEAT.

## [ThreeDimensionalAnimationBreeder](https://github.com/schrum2/MM-NEAT/blob/master/src/main/java/edu/southwestern/tasks/interactive/objectbreeder/ThreeDimensionalAnimationBreederTask.java)

ThreeDimensionalAnimationBreeder evolves original animating 3D objects by taking in an x-coordinate, a 
y-coordinate, a z-coordinate, a distance from the center, time, and a bias of 1.0 as inputs to the CPPN, and 
outputting whether there is a cube present and hue, saturation, and brightness values that are used to 
construct a cube of a specific color if the threshold is met for a cube to be present. The process is the same 
as the original ThreeDimensionalObjectBreeder, but the addition of a time input allows the shapes to vary over
time, creating an animation of constructed three-dimensional objects. 

* Developed using MM-NEAT.

## [SoundRemixer (prototype)](https://github.com/schrum2/MM-NEAT/blob/master/src/main/java/edu/southwestern/tasks/interactive/remixbreeder/SoundRemixTask.java)

SoundRemixer manipulates WAV files by taking in a time, sine of time, an input WAV file, and a bias of 1.0 as
inputs to the CPPN, and outputting an amplitude array that can be played back as a sound. The process of 
creating the sound is very similar to the generation of sound in the Breedesizer, but it manipulates an input
sound in this way. However, the results are not terribly appealing yet.

* Still under development.

## [PictureRemixer (prototype)](https://github.com/schrum2/MM-NEAT/blob/master/src/main/java/edu/southwestern/tasks/interactive/remixbreeder/PictureRemixTask.java)

PictureRemixer manipulates images by taking in an x-coordinate, a y-coordinate, the distance from the center,
the input picture's hue, saturation, and 
brightness, and a bias of 1.0 as inputs to the CPPN, and outputting hue, saturation, and 
brightness values that are used to plot the color at the input (x,y). The process of creating the image is very
similar to the generation of images in Picbreeder, but it manipulates an input image.
Currently, the results seem to merely be color shifts on the original image.

* Still under development.

## [Mario Levels (prototype)](https://github.com/schrum2/MM-NEAT/blob/master/src/main/java/edu/southwestern/tasks/interactive/mario/MarioLevelBreederTask.java)

Interactively evolves Mario levels. Users selectively breed levels based on appearance, but can also choose to play any of the evolved levels. 

* Still under development.

# Create your own interactive evolution task

To create your own original interactive evolution task, create a class that extends 
[InteractiveEvolutionTask](https://github.com/schrum2/MM-NEAT/blob/master/src/main/java/edu/southwestern/tasks/interactive/InteractiveEvolutionTask.java). 
The abstract methods from the task that you will need to override are getWindowTitle(), save(), 
getButtonImage(), additionalButtonClickAction(), getFileType(), getFileExtension(), numCPPNInputs(), and 
numCPPNOutputs(). Methods that are not necessary to override, but that you may need to override in order to 
match the specifications of your program, are respondToclick(), sensorLabels(), outputLabels(), 
setButtonImage(), save(), resetButton(), setEffectCheckbox(), evolve(), reset(), and setUndo(). 
