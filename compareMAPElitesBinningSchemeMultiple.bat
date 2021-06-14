REM Usage  : compareMAPElitesBinningSchemeMultiple.bat <experiment directory> <log prefix> <run type> <first run number> <last number> <number of trials per individual> <Binning Labels Package Path> <Number of threads>
REM example: compareMAPElitesBinningSchemeMultiple.bat mariocomparebins MarioCompareBins DecorateNSLeniency 0 9 1 edu.southwestern.tasks.mario.MarioMAPElitesDistinctChunksNSAndDecorationBinLabels 10

FOR /L %%A IN (%4,1,%5) DO (
  java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%%A parallelEvaluations:true threads:%8 base:%1 log:%2-%3 saveTo:%3 trials:%6 experiment:edu.southwestern.experiment.post.CompareMAPElitesBinningSchemeExperiment mapElitesBinLabels:%7 logLock:true io:false
)
ECHO "All done!"