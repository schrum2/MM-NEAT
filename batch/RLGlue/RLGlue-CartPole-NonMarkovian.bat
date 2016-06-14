cd ..
cd ..
setlocal enabledelayedexpansion
set string=java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:cartpole trials:1 maxGens:100 mu:50 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.rlglue.cartpole.CartPoleTask cleanOldNetworks:false fs:false noisyTaskStat:edu.utexas.cs.nn.util.stats.Average log:RL-NonMarkovian saveTo:NonMarkovian rlGlueEnvironment:org.rlcommunity.environments.cartpole.CartPole rlGlueExtractor:edu.utexas.cs.nn.tasks.rlglue.featureextractors.cartpole.NonMarkovianCartPoleExtractor 
set /a r=%1
set /a r=!r!+4116
set string=!string! rlGluePort:
set string=!string!!r!
call !string!