cd ..
cd ..
java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:match trials:1 maxGens:1000 mu:100 io:true netio:true mating:true fs:false task:edu.utexas.cs.nn.tasks.testmatch.imagematch.ImageMatchTask log:FrenchFlag-BDDS saveTo:BDDS matchImageFile:TeenyTinyFrenchFlag.png allowMultipleFunctions:true ftype:0 watch:false netChangeActivationRate:0.3 overrideImageSize:true imageHeight:198 imageWidth:300 ea:edu.utexas.cs.nn.evolution.nsga2.bd.BDNSGA2 behaviorCharacterization:edu.utexas.cs.nn.evolution.nsga2.bd.characterizations.DomainSpecificCharacterization
