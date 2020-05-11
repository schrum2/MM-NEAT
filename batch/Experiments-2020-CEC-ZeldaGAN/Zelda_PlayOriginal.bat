START "Recording" Record.bat Subject-%1\Subject-%1-OriginalLevel
cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar zeldaType:original randomSeed:%1 > batch/Experiments-2020-CEC-ZeldaGAN/Subject-%1/Subject-%1-OriginalDungeon.txt
exit