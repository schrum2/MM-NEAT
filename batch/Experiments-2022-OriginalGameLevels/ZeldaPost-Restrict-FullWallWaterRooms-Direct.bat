cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 parallelEvaluations:true threads:20 base:zeldadungeonswallwaterrooms log:ZeldaDungeonsWallWaterRooms-FullDirect2GAN saveTo:FullDirect2GAN trials:1 experiment:edu.southwestern.experiment.post.CompareMAPElitesBinningSchemeExperiment mapElitesBinLabels:edu.southwestern.tasks.zelda.ZeldaMAPElitesWallWaterRoomsBinLabels logLock:true io:false zeldaMinWallIndex:0 zeldaMaxWallIndex:1 zeldaMinWaterIndex:0 zeldaMaxWaterIndex:1 zeldaMinReachableRooms:17 zeldaMaxReachableRooms:57