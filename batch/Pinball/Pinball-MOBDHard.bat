cd ..
cd ..
java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:pinball trials:10 maxGens:500 mu:100 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.pinball.PinballTask cleanOldNetworks:true fs:false log:Pinball-MOBDHard saveTo:MOBDHard watch:false pinballConfig:pinball_hard_single.cfg moPinball:true ea:edu.utexas.cs.nn.evolution.nsga2.bd.BDNSGA2