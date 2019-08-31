START Record.bat Subject-%1\Subject-%1-GraphGAN
cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar zeldaType:generated randomSeed:%1 zeldaLevelLoader:edu.southwestern.tasks.gvgai.zelda.level.GANLoader > batch/Experiments-2019-ZeldaGAN/Subject-%1/Subject-%1-GeneratedGAN.txt
SLEEP 1000
taskkill /F /IM ffmpeg.exe /T
exit