cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:tetris trials:3 maxGens:500 mu:50 io:true netio:true mating:true task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask  rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor tetrisTimeSteps:true tetrisBlocksOnScreen:false  rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent splitRawTetrisInputs:true senseHolesDifferently:true hyperNEAT:true genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNAndSubstrateArchitectureGenotype allowMultipleFunctions:true ftype:1 netChangeActivationRate:0.3 substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping steps:500000 perLinkMutateRate:0.05 netLinkRate:0.4 netSpliceRate:0.2 crossoverRate:0.5 log:Tetris-CNNDepth1Width1SZGlobalSwitch100 saveTo:CNNDepth1Width1SZGlobalSwitch100 extraHNLinks:true HNProcessDepth:1 HNProcessWidth:1 convolution:false tetrisAllowLine:false tetrisAllowSquare:false tetrisAllowTri:false tetrisAllowLShape:false tetrisAllowJShape:false senseTetrisHolesAsPositive:true cascadeExpansion:true cascadeExpansionSwitchGeneration:100 substrateLocationInputs:true substrateBiasLocationInputs:true