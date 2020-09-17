REM Usage:   postReplay.bat <recording save file> <module to show trails for> <max level> <ms per time step>
REM Example: postReplay.bat OneLifeConflict-OneModule0.rec -1 4 15
cd ..
cd ..
java -jar "target/MM-NEAT-0.0.1-SNAPSHOT.jar" trials:1 watch:true showNetworks:false io:false netio:false onlyWatchPareto:true printFitness:true animateNetwork:false monitorInputs:false viewModePreference:false experiment:edu.southwestern.experiment.post.BestNetworkExperiment logLock:true evalReport:false scentMode:%2 modePheremone:true replayPacman:true pacmanSaveFile:%1 pacmanMaxLevel:%3 pacmanReplayDelay:%4 stepByStep:false
