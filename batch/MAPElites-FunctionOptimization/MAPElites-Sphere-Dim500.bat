cd ..
cd ..
java -ea -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 io:true base:mapelitesfunctionoptimization log:mapelitesfunctionoptimization-MAPElitesSphereFunctionOptimization saveTo:MAPElitesSphereFunctionOptimization netio:false polynomialMutation:false maxGens:2497500 ea:edu.southwestern.evolution.mapelites.MAPElites task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.SphereFunction steadyStateIndividualsPerGeneration:10000 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRangeBinLabels foBinDimension:500 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12 mapElitesQDBaseOffset:525