cd ..
cd ..
setlocal enabledelayedexpansion
set string=java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:tetris trials:5 maxGens:500 mu:50 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.rlglue.tetris.TetrisTask cleanOldNetworks:true fs:false noisyTaskStat:edu.utexas.cs.nn.util.stats.Average log:RL-moRawSplitInputs saveTo:moRawSplitInputs rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris rlGlueExtractor:edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor tetrisTimeSteps:true tetrisBlocksOnScreen:true rlGlueAgent:edu.utexas.cs.nn.tasks.rlglue.tetris.TetrisAfterStateAgent splitRawTetrisInputs:true senseHolesDifferently:true
set /a r=%1
set /a r=!r!+4196
set string=!string! rlGluePort:
set string=!string!!r!
call !string!