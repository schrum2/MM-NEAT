cd..
cd..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 ^
minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.DirectRepresentationShapeGenerator ^
genotype:edu.southwestern.tasks.evocraft.genotype.MinecraftShapeGenotype vectorPresenceThresholdForEachBlock:true ^
trials:1 io:true netio:true mating:true rememberParentScores:true ^
watch:false saveAllChampions:true ^
parallelEvaluations:true threads:10 ^
launchMinecraftServerFromJava:false minecraftClearSleepTimer:400 minecraftSkipInitialClear:true extraSpaceBetweenMinecraftShapes:100 ^
task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask ^
minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false interactWithMapElitesInWorld:false ^
mu:20 maxGens:3005 ^
minecraftMandatoryWaitTime:10000 ^
minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 ^
minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet ^
spaceBetweenMinecraftShapes:22 ^
minecraftClearWithGlass:false ^
minecraftAccumulateChangeInCenterOfMass:true minecraftChangeCenterOfMassFitness:true ^
minecraftWeightedSumsAccumulateNewBlockPositionsAndChangeCenterOfMassFitness:false ^
base:minecraftcomplex log:MinecraftComplex-SmallFlyPF saveTo:SmallFlyPF
