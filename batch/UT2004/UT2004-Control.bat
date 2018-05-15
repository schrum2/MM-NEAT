cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:0 base:ut2004 trials:3 maxGens:100 mu:50 io:true netio:true mating:true cleanOldNetworks:false log:UT2004-Control saveTo:Control experiment:edu.southwestern.experiment.evolution.LimitedSinglePopulationGenerationalEAExperiment ea:edu.southwestern.evolution.nsga2.NSGA2 genotype:edu.southwestern.evolution.genotypes.TWEANNGenotype crossover:edu.southwestern.evolution.crossover.network.TWEANNCrossover utDrive:C utPath:UT2004 utMap:DM-TrainingDay utSensorModel:edu.southwestern.tasks.ut2004.sensors.OpponentRelativeSensorModel utOutputModel:edu.southwestern.tasks.ut2004.actuators.OpponentRelativeMovementOutputModel utWeaponManager:edu.southwestern.tasks.ut2004.weapons.SimpleWeaponManager task:edu.southwestern.tasks.ut2004.UT2004OneVsNativeBotsDeathMatchTask parallelEvaluations:true utEvolvingBotSkill:7 utNativeBotSkill:7 utEvalMinutes:5 threads:2