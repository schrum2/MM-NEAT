cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:puddle trials:1 maxGens:500 mu:50 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.rlglue.puddleworld.PuddleWorldTask cleanOldNetworks:false fs:false noisyTaskStat:edu.utexas.cs.nn.util.stats.Average log:RL-Puddle saveTo:Puddle rlGlueEnvironment:org.rlcommunity.environments.puddleworld.PuddleWorld moPuddleWorld:true
