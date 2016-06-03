cd ..
cd ..
java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:vizdoomdc trials:5 maxGens:100 mu:50 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.vizdoom.VizDoomDefendCenterTask cleanOldNetworks:true fs:false noisyTaskStat:edu.utexas.cs.nn.util.stats.Average log:DefendCenter-Control saveTo:Control doomEpisodeLength:2100 watch:false