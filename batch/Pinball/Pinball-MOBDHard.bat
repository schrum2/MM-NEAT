cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:pinball trials:10 maxGens:500 mu:100 io:true netio:true mating:true task:edu.southwestern.tasks.pinball.PinballTask cleanOldNetworks:true fs:false log:Pinball-MOBDHard saveTo:MOBDHard watch:false pinballConfig:pinball_hard_single.cfg moPinball:true ea:edu.southwestern.evolution.nsga2.bd.BDNSGA2