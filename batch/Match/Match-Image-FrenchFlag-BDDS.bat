cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:match trials:1 maxGens:1000 mu:100 io:true netio:true mating:true fs:false task:edu.southwestern.tasks.testmatch.imagematch.ImageMatchTask log:FrenchFlag-BDDS saveTo:BDDS matchImageFile:TeenyTinyFrenchFlag.png allowMultipleFunctions:true ftype:0 watch:false netChangeActivationRate:0.3 overrideImageSize:true imageHeight:198 imageWidth:300 ea:edu.southwestern.evolution.nsga2.bd.BDNSGA2 behaviorCharacterization:edu.southwestern.evolution.nsga2.bd.characterizations.DomainSpecificCharacterization
