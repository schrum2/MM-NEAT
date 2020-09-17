REM Usage:   postBestFourMazeWatch.bat <experiment directory> <log prefix> <run type> <run number> <number of trials per individual>
REM Example: postBestFourMazeWatch.bat onelifeconflict OneLifeConflict OneModule 0 5
cd ..
cd ..
java -jar "target/MM-NEAT-0.0.1-SNAPSHOT.jar" runNumber:%4 parallelEvaluations:false experiment:edu.southwestern.experiment.post.BestNetworkExperiment base:%1 log:%2-%3 saveTo:%3 trials:%5 watch:true showNetworks:true io:false netio:false onlyWatchPareto:true printFitness:false pacManGainsLives:true pacmanLives:3 animateNetwork:false monitorInputs:true logDeathLocations:false pacManLevelTimeLimit:50000 evalReport:true timedPacman:true modePheremone:true observePacManPO:false
