cd ..
cd ..
setlocal enabledelayedexpansion
set string=java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:puddle trials:1 maxGens:500 mu:50 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.rlglue.puddleworld.PuddleWorldTask cleanOldNetworks:false fs:false noisyTaskStat:edu.utexas.cs.nn.util.stats.Average log:RL-Puddle saveTo:Puddle rlGlueEnvironment:org.rlcommunity.environments.puddleworld.PuddleWorld moPuddleWorld:true
set /a r=%1
set /a r=!r!+4096
set string=!string! rlGluePort:
set string=!string!!r!
call !string!