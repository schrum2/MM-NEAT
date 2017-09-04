cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:fof trials:3 maxGens:500 mu:100 io:true netio:true mating:false fs:true task:edu.southwestern.tasks.breve2D.Breve2DTask breveDynamics:edu.southwestern.breve2D.dynamics.FightOrFlight breveEnemy:edu.southwestern.breve2D.agent.PredatorPreyEnemy log:FoF-Control saveTo:Control