START "Recording" Record.bat Subject-%1\Subject-%1-GraphOriginalRooms
cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar zeldaType:generated randomSeed:%1 zeldaLevelLoader:edu.southwestern.tasks.gvgai.zelda.level.OriginalLoader > batch/Experiments-2020-CEC-ZeldaGAN/Subject-%1/Subject-%1-GeneratedOriginal.txt
exit