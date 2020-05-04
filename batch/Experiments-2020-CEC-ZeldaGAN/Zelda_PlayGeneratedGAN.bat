START "Recording" Record.bat Subject-%1\Subject-%1-GraphGAN
cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar zeldaType:generated randomSeed:%1 zeldaLevelLoader:edu.southwestern.tasks.gvgai.zelda.level.GANLoader > batch/Experiments-2020-CEC-ZeldaGAN/Subject-%1/Subject-%1-GeneratedGAN.txt
exit