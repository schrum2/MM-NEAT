cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:megamanlevels log:MegaManLevels-ConnectivityOneGAN saveTo:ConnectivityOneGAN watch:false GANInputSize:5 trials:1 mu:100 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.megaman.MegaManGANLevelTask useMultipleGANsMegaMan:false MegaManGANModel:MegaManOneGANWith12Tiles_5_Epoch5000.pth megaManGANLevelChunks:10 megaManAllowsSimpleAStarPath:false megaManAllowsConnectivity:true megaManAllowsLeftSegments:true cleanFrequency:-1 saveAllChampions:true, cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000