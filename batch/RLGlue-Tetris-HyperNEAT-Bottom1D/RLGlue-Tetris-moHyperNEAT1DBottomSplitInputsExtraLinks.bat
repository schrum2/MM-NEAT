cd ..
cd ..
setlocal enabledelayedexpansion
set string=java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:tetris trials:5 maxGens:500 mu:50 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.rlglue.tetris.HyperNEATTetrisTask cleanOldNetworks:true fs:false noisyTaskStat:edu.utexas.cs.nn.util.stats.Average log:RL-moHyperNEAT1DBottomSplitInputsExtraLinks saveTo:moHyperNEAT1DBottomSplitInputsExtraLinks rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris rlGlueExtractor:edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor tetrisTimeSteps:true tetrisBlocksOnScreen:false rlGlueAgent:edu.utexas.cs.nn.tasks.rlglue.tetris.TetrisAfterStateAgent hyperNEAT:true genotype:edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype allowMultipleFunctions:true ftype:1 netChangeActivationRate:0.3 absenceNegative:false substrateMapping:edu.utexas.cs.nn.networks.hyperneat.Bottom1DSubstrateMapping splitRawTetrisInputs:true senseHolesDifferently:true extraHNLinks:true
set /a r=%1
set /a r=!r!+4236
set string=!string! rlGluePort:
set string=!string!!r!
call !string!