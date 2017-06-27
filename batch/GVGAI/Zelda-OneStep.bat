cd ..
cd ..
java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:gvgai trials:10 maxGens:500 mu:100 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.gvgai.GVGAISinglePlayerTask cleanOldNetworks:true fs:false log:Zelda-ZeldaOneStep saveTo:ZeldaOneStep gvgaiGame:zelda gvgaiLevel:0 gvgaiPlayer:edu.utexas.cs.nn.tasks.gvgai.GVGAIOneStepNNPlayer