cd ..
cd ..
java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:mario trials:5 maxGens:100 mu:50 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.mario.MarioTask cleanOldNetworks:true fs:false log:Mario-Basic saveTo:Basic watch:false 