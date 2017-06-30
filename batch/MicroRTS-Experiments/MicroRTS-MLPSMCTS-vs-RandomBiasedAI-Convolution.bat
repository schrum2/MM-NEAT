cd ..
cd ..
java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:microRTS trials:3 maxGens:500 mu:10 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.microrts.MicroRTSTask cleanOldNetworks:true fs:false watch:false microRTSEvaluationFunction:edu.utexas.cs.nn.tasks.microrts.evaluation.NNComplexEvaluationFunction map:12x12/basesWorkers12x12.xml microRTSFitnessFunction:edu.utexas.cs.nn.tasks.microrts.fitness.ProgressiveFitnessFunction hyperNEAT:true genotype:edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype allowMultipleFunctions:true ftype:1 netChangeActivationRate:0.3 extraHNLinks:true mRTSAllSqrt3MobileUnits:true mRTSMyBuildingGradientMobileUnits:true mRTSResourceProportion:true extraHNLinks:true HNProcessWidth:3  microRTSAgent:micro.ai.mcts.mlps.MLPSMCTS microRTSOpponent:micro.ai.RandomBiasedAI log:MicroRTS-ExperimentMLPSvsRandConvolution saveTo:ExperimentMLPSvsRandConvolution convolution:true