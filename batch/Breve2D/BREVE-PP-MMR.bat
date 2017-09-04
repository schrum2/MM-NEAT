cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:pp trials:3 maxGens:500 mu:100 io:true netio:true mating:false fs:true task:edu.utexas.cs.nn.tasks.breve2D.Breve2DTask breveDynamics:edu.utexas.cs.nn.breve2D.dynamics.PredatorPrey breveEnemy:edu.utexas.cs.nn.breve2D.agent.PredatorPreyEnemy log:PP-MMR saveTo:MMR mmrRate:0.1