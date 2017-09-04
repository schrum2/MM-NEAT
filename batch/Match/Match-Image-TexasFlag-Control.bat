cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:match trials:1 maxGens:50 mu:100 io:true netio:true mating:true fs:false task:edu.utexas.cs.nn.tasks.testmatch.imagematch.ImageMatchTask log:Image-TexasFlag saveTo:TexasFlag matchImageFile:TEXASSSSSS.png allowMultipleFunctions:true ftype:0 watch:false netChangeActivationRate:0.3 saveAllChampions:true