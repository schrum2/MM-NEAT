cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:cartpole trials:1 maxGens:100 mu:50 io:true netio:true mating:true task:edu.southwestern.tasks.rlglue.cartpole.CartPoleTask cleanOldNetworks:false fs:false noisyTaskStat:edu.southwestern.util.stats.Average log:RL-NonMarkovian saveTo:NonMarkovian rlGlueEnvironment:org.rlcommunity.environments.cartpole.CartPole rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.cartpole.NonMarkovianCartPoleExtractor 
