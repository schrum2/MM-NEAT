cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:acrobot trials:1 maxGens:500 mu:50 io:true netio:true mating:true task:edu.southwestern.tasks.rlglue.acrobot.AcrobotTask cleanOldNetworks:false fs:false noisyTaskStat:edu.southwestern.util.stats.Average log:RL-Acrobot saveTo:Acrobot rlGlueEnvironment:org.rlcommunity.environments.acrobot.Acrobot
