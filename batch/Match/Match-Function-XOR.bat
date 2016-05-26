cd ..
cd ..
java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:match trials:1 maxGens:100 mu:100 io:true netio:true mating:true fs:true task:edu.utexas.cs.nn.tasks.testmatch.functions.XORTask log:Function-XOR saveTo:XOR