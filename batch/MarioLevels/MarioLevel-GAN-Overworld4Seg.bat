cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:mariogan trials:1 mu:50 maxGens:500 io:true netio:true mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask log:MarioGAN-Overworld4Seg saveTo:Overworld4Seg saveAllChampions:true logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_30_Epoch5000.pth GANInputSize:30 marioGANLevelChunks:4
