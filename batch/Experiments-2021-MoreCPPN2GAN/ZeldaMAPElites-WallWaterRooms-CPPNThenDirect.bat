cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 zeldaCPPN2GANSparseKeys:true zeldaALlowPuzzleDoorUglyHack:false zeldaCPPNtoGANAllowsRaft:true zeldaCPPNtoGANAllowsPuzzleDoors:true zeldaDungeonBackTrackRoomFitness:true zeldaDungeonDistinctRoomFitness:true zeldaDungeonDistanceFitness:false zeldaDungeonFewRoomFitness:false zeldaDungeonTraversedRoomFitness:true zeldaPercentDungeonTraversedRoomFitness:false zeldaDungeonRandomFitness:false watch:false trials:1 mu:100 makeZeldaLevelsPlayable:false base:zeldadungeonswallwaterrooms log:ZeldaDungeonsWallWaterRooms-CPPNThenDirect2GAN saveTo:CPPNThenDirect2GAN zeldaGANLevelWidthChunks:5 zeldaGANLevelHeightChunks:5 zeldaGANModel:ZeldaFixedDungeonsAll_5000_10.pth maxGens:100000 io:true netio:true GANInputSize:10 mating:true fs:false task:edu.southwestern.tasks.zelda.ZeldaCPPNOrDirectToGANDungeonTask indirectToDirectTransitionRate:0.1 cleanOldNetworks:false zeldaGANUsesOriginalEncoding:false allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:-1 recurrency:false saveAllChampions:true includeFullSigmoidFunction:true includeFullGaussFunction:true includeCosineFunction:true includeGaussFunction:false includeIdFunction:true includeTriangleWaveFunction:true includeSquareWaveFunction:true includeFullSawtoothFunction:true includeSigmoidFunction:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.zelda.ZeldaMAPElitesWallWaterRoomsBinLabels steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype netChangeActivationRate:0.3  logMutationAndLineage:true