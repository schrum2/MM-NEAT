REM Usage:   postBestMPMvsGRecord.bat <experiment directory> <log prefix> <run type> <run number> <recording save file>
REM Example: postBestMPMvsGRecord.bat onelifeconflict OneLifeConflict OneModule 0 OneLifeConflict-OneModule0.rec
cd ..
cd ..
java -jar "target/MM-NEAT-0.0.1-SNAPSHOT.jar" runNumber:%4 experiment:edu.utexas.cs.nn.experiment.post.BestNetworkExperiment base:%1 log:%2-%3 saveTo:%3 trials:1 watch:true showNetworks:false io:false netio:false onlyWatchPareto:true printFitness:false pacManGainsLives:true pacmanLives:3 animateNetwork:false monitorInputs:false logDeathLocations:false pacManLevelTimeLimit:3000 pacmanMaxLevel:16 pacmanFatalTimeLimit:false evalReport:true timedPacman:true getRemainingPills:true modePheremone:true logLock:true evalReport:true recordPacman:true pacmanSaveFile:%5 
