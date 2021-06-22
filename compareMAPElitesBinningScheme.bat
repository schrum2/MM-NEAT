REM Usage:   <experiment directory> <log prefix> <run type> <run number> <number of trials per individual> <Binning Labels Package Path>
REM Example: mariolevelsdecoratensleniency MarioLevelsDecorateNSLeniency CPPNThenDirect2GAN 0 1 edu.southwestern.tasks.mario.MarioMAPElitesDistinctChunksNSAndDecorationBinLabels 10
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%4 parallelEvaluations:true threads:%7 base:%1 log:%2-%3 saveTo:%3 trials:%5 experiment:edu.southwestern.experiment.post.CompareMAPElitesBinningSchemeExperiment mapElitesBinLabels:%6 logLock:true io:false