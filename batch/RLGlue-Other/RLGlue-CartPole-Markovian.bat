cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:cartpole trials:1 maxGens:10 mu:50 io:true netio:true mating:true task:edu.southwestern.tasks.rlglue.cartpole.CartPoleTask cleanOldNetworks:false fs:false noisyTaskStat:edu.southwestern.util.stats.Average log:RL-Markovian saveTo:Markovian rlGlueEnvironment:org.rlcommunity.environments.cartpole.CartPole
