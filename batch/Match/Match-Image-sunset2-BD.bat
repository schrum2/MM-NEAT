cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:sunset2 trials:1 maxGens:500 mu:100 io:true netio:true mating:true fs:false task:edu.southwestern.tasks.testmatch.imagematch.ImageMatchTask log:sunset2-BD saveTo:BD matchImageFile:sunset2.png allowMultipleFunctions:true ftype:0 watch:false netChangeActivationRate:0.3 overrideImageSize:false imageHeight:133 imageWidth:200 saveAllChampions:true ea:edu.southwestern.evolution.nsga2.bd.BDNSGA2 behaviorCharacterization:edu.southwestern.evolution.nsga2.bd.characterizations.GeneralNetworkCharacterization
