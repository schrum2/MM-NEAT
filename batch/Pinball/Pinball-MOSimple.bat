cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:pinball trials:10 maxGens:500 mu:100 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.pinball.PinballTask cleanOldNetworks:true fs:false log:Pinball-MOSimple saveTo:MOSimple watch:false pinballConfig:pinball_simple_single.cfg moPinball:true