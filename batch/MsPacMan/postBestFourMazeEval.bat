REM Usage:   postBestFourMazeEval.bat <experiment directory> <log prefix> <run type> <run number> <number of trials per individual>
REM Example: postBestFourMazeEval.bat onelifeconflict OneLifeConflict OneModule 0 5
cd ..
cd ..
java -jar "target/MM-NEAT-0.0.1-SNAPSHOT.jar" runNumber:%4 parallelEvaluations:false experiment:edu.southwestern.experiment.post.BestNetworkExperiment base:%1 log:%2-%3 saveTo:%3 trials:%5 watch:false showNetworks:false io:false netio:false onlyWatchPareto:true printFitness:false pacManGainsLives:true pacmanLives:3 animateNetwork:false monitorInputs:false logDeathLocations:false pacManLevelTimeLimit:50000 evalReport:true timedPacman:true
