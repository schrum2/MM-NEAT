cd ..
cd ..
setlocal enabledelayedexpansion
set string=java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:mountaincar trials:1 maxGens:500 mu:50 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.rlglue.mountaincar.MountainCarTask cleanOldNetworks:false fs:false noisyTaskStat:edu.utexas.cs.nn.util.stats.Average log:RL-MountainCar saveTo:MountainCar rlGlueEnvironment:org.rlcommunity.environments.mountaincar.MountainCar
set /a r=%1
set /a r=!r!+4096
set string=!string! rlGluePort:
set string=!string!!r!
call !string!