cd ..
cd ..
java -jar dist/MM-NEATv2.jar runNumber:0 randomSeed:0 base:HNMsPacMan maxGens:200 mu:2 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.mspacman.MsPacManTask cleanOldNetworks:true pacManLevelTimeLimit:8000 pacmanInputOutputMediator:edu.utexas.cs.nn.tasks.mspacman.sensors.MsPacManHyperNEATMediator trials:1 log:HNMsPacMan-OneLifeSplit saveTo:OneLifeSplit hyperNEAT:true genotype:edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype allowMultipleFunctions:true fs:true ftype:1 netChangeActivationRate:0.3 
