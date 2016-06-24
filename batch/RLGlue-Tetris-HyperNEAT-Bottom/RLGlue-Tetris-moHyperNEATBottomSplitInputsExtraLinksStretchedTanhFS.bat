cd ..
cd ..
setlocal enabledelayedexpansion
set string=java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:tetris trials:3 maxGens:500 mu:50 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.rlglue.tetris.HyperNEATTetrisTask cleanOldNetworks:true noisyTaskStat:edu.utexas.cs.nn.util.stats.Average log:RL-moHyperNEATBottomSplitInputsExtraLinksStretchedTanhFS saveTo:moHyperNEATBottomSplitInputsExtraLinksStretchedTanhFS rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris rlGlueExtractor:edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor tetrisTimeSteps:true tetrisBlocksOnScreen:false rlGlueAgent:edu.utexas.cs.nn.tasks.rlglue.tetris.TetrisAfterStateAgent hyperNEAT:true genotype:edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype allowMultipleFunctions:true ftype:17 netChangeActivationRate:0.3 absenceNegative:false substrateMapping:edu.utexas.cs.nn.networks.hyperneat.BottomSubstrateMapping splitRawTetrisInputs:true senseHolesDifferently:true extraHNTetrisLinks:true fs:true
set /a r=%1
set /a r=!r!+4246
set string=!string! rlGluePort:
set string=!string!!r!
call !string!