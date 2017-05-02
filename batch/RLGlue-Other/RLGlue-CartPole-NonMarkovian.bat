cd ..
cd ..
java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:cartpole trials:1 maxGens:100 mu:50 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.rlglue.cartpole.CartPoleTask cleanOldNetworks:false fs:false noisyTaskStat:edu.utexas.cs.nn.util.stats.Average log:RL-NonMarkovian saveTo:NonMarkovian rlGlueEnvironment:org.rlcommunity.environments.cartpole.CartPole rlGlueExtractor:edu.utexas.cs.nn.tasks.rlglue.featureextractors.cartpole.NonMarkovianCartPoleExtractor 
