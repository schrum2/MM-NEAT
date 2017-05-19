cd ..
cd ..
java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:microRTS trials:1 maxGens:500 mu:100 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.microrts.MicroRTSTask cleanOldNetworks:true fs:false log:MicroRTS-Basic saveTo:Basic watch:false