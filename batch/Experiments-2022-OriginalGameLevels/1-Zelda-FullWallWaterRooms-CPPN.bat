cd ..
java -jar MMNEAT.jar runNumber:%1 randomSeed:%1 zeldaCPPN2GANSparseKeys:true zeldaALlowPuzzleDoorUglyHack:false zeldaCPPNtoGANAllowsRaft:true zeldaCPPNtoGANAllowsPuzzleDoors:true zeldaDungeonBackTrackRoomFitness:true zeldaDungeonDistinctRoomFitness:true zeldaDungeonDistanceFitness:false zeldaDungeonFewRoomFitness:false zeldaDungeonTraversedRoomFitness:true zeldaPercentDungeonTraversedRoomFitness:true zeldaDungeonRandomFitness:false watch:false trials:1 mu:100 makeZeldaLevelsPlayable:false base:zeldadungeonswallwaterrooms log:ZeldaDungeonsWallWaterRooms-FullCPPN2GAN saveTo:FullCPPN2GAN zeldaGANLevelWidthChunks:8 zeldaGANLevelHeightChunks:8 zeldaGANModel:ZeldaDungeonsAll3Tiles_10000_10.pth maxGens:50000 io:true netio:true GANInputSize:10 mating:true fs:false task:edu.southwestern.tasks.zelda.ZeldaCPPNtoGANDungeonTask cleanOldNetworks:false zeldaGANUsesOriginalEncoding:false allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:-1 recurrency:false saveAllChampions:true includeFullSigmoidFunction:true includeFullGaussFunction:true includeCosineFunction:true includeGaussFunction:false includeIdFunction:true includeTriangleWaveFunction:true includeSquareWaveFunction:true includeFullSawtoothFunction:true includeSigmoidFunction:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.zelda.ZeldaMAPElitesWallWaterRoomsBinLabels steadyStateIndividualsPerGeneration:100 zeldaMinWallIndex:0 zeldaMaxWallIndex:1 zeldaMinWaterIndex:0 zeldaMaxWaterIndex:1 zeldaMinReachableRooms:17 zeldaMaxReachableRooms:57 discardFromBinOutsideRestrictedRange:false