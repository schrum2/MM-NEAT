cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:microRTS trials:1 maxGens:500 mu:6 io:true netio:true mating:true task:edu.southwestern.tasks.microrts.MicroRTSTask cleanOldNetworks:true fs:false log:MicroRTS-Watch saveTo:Watch watch:true