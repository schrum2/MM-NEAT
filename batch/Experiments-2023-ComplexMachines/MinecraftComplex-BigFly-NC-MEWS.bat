cd..
cd..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 ^
minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.DirectRepresentationShapeGenerator ^
genotype:edu.southwestern.tasks.evocraft.genotype.MinecraftShapeGenotype vectorPresenceThresholdForEachBlock:true ^
trials:1 io:true netio:true mating:true rememberParentScores:true ^
watch:false saveAllChampions:true ^
parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true ^
launchMinecraftServerFromJava:false minecraftClearSleepTimer:400 minecraftSkipInitialClear:true extraSpaceBetweenMinecraftShapes:100 ^
task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask ^
minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false interactWithMapElitesInWorld:false ^
experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 ^
mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBlockCountEmptyCountBinLabels ^
mapElitesQDBaseOffset:1 ^
ea:edu.southwestern.evolution.mapelites.MAPElites ^
mu:100 maxGens:60000 ^
minecraftMandatoryWaitTime:10000 ^
minecraftXRange:5 minecraftYRange:5 minecraftZRange:5 ^
minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet ^
spaceBetweenMinecraftShapes:22 ^
minecraftClearWithGlass:false ^
minecraftAccumulateChangeInCenterOfMass:true minecraftChangeCenterOfMassFitness:false minecraftAccumulateNewBlockPositionsFitness:false ^
minecraftWeightedSumsAccumulateNewBlockPositionsAndChangeCenterOfMassFitness:true ^
base:minecraftcomplex log:MinecraftComplex-BigFlyNCMEWS saveTo:BigFlyNCMEWS

