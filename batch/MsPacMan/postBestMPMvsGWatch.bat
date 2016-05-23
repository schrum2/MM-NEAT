REM Usage:   postBestMPMvsGWatch.bat <experiment directory> <log prefix> <run type> <run number> <number of trials per individual>
REM Example: postBestMPMvsGWatch.bat onelifeconflict OneLifeConflict OneModule 0 5
cd ..
cd ..
java -jar "dist/MM-NEATv2.jar" runNumber:%4 experiment:edu.utexas.cs.nn.experiment.BestNetworkExperiment base:%1 log:%2-%3 saveTo:%3 trials:%5 watch:true showNetworks:true io:false netio:false onlyWatchPareto:true printFitness:false pacManGainsLives:true pacmanLives:3 animateNetwork:false monitorInputs:true logDeathLocations:false pacManLevelTimeLimit:3000 pacmanMaxLevel:16 pacmanFatalTimeLimit:false evalReport:true timedPacman:true getRemainingPills:true modePheremone:true
