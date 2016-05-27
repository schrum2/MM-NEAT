cd ..
cd ..
java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:cartpole trials:1 maxGens:100 mu:50 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.rlglue.RLGlueTask cleanOldNetworks:false fs:false noisyTaskStat:edu.utexas.cs.nn.util.stats.Average log:RL-Markovian saveTo:Markovian rlGlueEnvironment:org.rlcommunity.environments.cartpole.CartPole