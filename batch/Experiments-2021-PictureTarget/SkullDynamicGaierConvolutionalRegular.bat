cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:targetimage mu:20 maxGens:10000000 steadyStateIndividualsPerGeneration:500 io:true netio:true mating:true task:edu.southwestern.tasks.innovationengines.PictureTargetTask allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:400 recurrency:false logTWEANNData:false logMutationAndLineage:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment fs:true useWoolleyImageMatchFitness:false useRMSEImageMatchFitness:true fitnessSaveThreshold:1.0 imageArchiveSaveFrequency:25000 includeSigmoidFunction:true includeTanhFunction:false includeIdFunction:true includeFullApproxFunction:false includeApproxFunction:false includeGaussFunction:true includeSineFunction:true includeCosineFunction:true includeSawtoothFunction:false includeAbsValFunction:false includeHalfLinearPiecewiseFunction:false includeStretchedTanhFunction:false includeReLUFunction:false includeSoftplusFunction:false includeLeakyReLUFunction:false includeFullSawtoothFunction:false includeTriangleWaveFunction:false includeSquareWaveFunction:false blackAndWhitePicbreeder:false deleteOldArchives:true randomInitialMutationChances:20 numReconstructionLossBins:32 dynamicAutoencoderIntervals:true trainInitialAutoEncoder:true trainingAutoEncoder:true convolutionalAutoencoder:true  mapElitesBinLabels:edu.southwestern.tasks.innovationengines.GaierAutoencoderPictureBinLabels log:TargetImage-SkullDynamicGaierConvolutionalRegular saveTo:SkullDynamicGaierConvolutionalRegular matchImageFile:skull64.jpg 