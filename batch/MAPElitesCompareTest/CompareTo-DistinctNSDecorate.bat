cd ..
cd ..
REM Usage:   <experiment directory> <log prefix> <run type> <run number> <number of trials per individual>
REM Example: onelifeconflict OneLifeConflict OneModule 0 5
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%4 parallelEvaluations:false base:%1 log:%2-%3 saveTo:%3 trials:%5 experiment:edu.southwestern.experiment.post.CompareMAPElitesBinningSchemeExperiment mapElitesBinLabels:edu.southwestern.tasks.mario.MarioMAPElitesDistinctChunksNSAndDecorationBinLabels