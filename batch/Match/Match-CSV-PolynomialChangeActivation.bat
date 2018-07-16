cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:match trials:1 maxGens:200 mu:100 io:true netio:true mating:true fs:true logMutationAndLineage:true recurrency:false task:edu.southwestern.tasks.testmatch.CSVRegressionTask log:CSV-PolynomialChangeActivation saveTo:PolynomialChangeActivation allowMultipleFunctions:true netChangeActivationRate:0.3
