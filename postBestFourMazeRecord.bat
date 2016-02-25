REM Usage:   postBestFourMazeRecord.bat <experiment directory> <log prefix> <run type> <run number> <recording save file>
REM Example: postBestFourMazeRecord.bat onelifeconflict OneLifeConflict OneModule 0 OneLifeConflict-OneModule0.rec
java -jar "dist/MM-NEATv2.jar" runNumber:%4 parallelEvaluations:false experiment:edu.utexas.cs.nn.experiment.BestNetworkExperiment base:%1 log:%2-%3 saveTo:%3 trials:1 watch:true showNetworks:false io:false netio:false onlyWatchPareto:true printFitness:false pacManGainsLives:true pacmanLives:3 animateNetwork:false monitorInputs:false logDeathLocations:false pacManLevelTimeLimit:50000 evalReport:true timedPacman:true modePheremone:true logLock:true evalReport:true recordPacman:true pacmanSaveFile:%5 modePheremone:true

