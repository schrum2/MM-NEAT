cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:mariogan trials:1 mu:50 maxGens:500 io:true netio:true mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask log:MarioGAN-CompactOverworld10Seg saveTo:CompactOverworld10Seg saveAllChampions:true marioStuckTimeout:20 watch:false genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 marioGANLevelChunks:10
