REM Usage:   SpawnMinecraftBlocks.bat <filename>
REM Example: SpawnMinecraftBlocks.bat 99 BlockCount NegativeSpace BlockCount0NegativeSpace1_454.40000_98.txt
cd..
cd..
java -ea -jar "target/MM-NEAT-0.0.1-SNAPSHOT.jar" base:%1 saveTo:%2 runNumber:%3 log:Minecraft-%2 minecraftBlockListTextFile:%4 launchMinecraftServerFromJava:false io:false netio:false launchMinecraftServerFromJava:false experiment:edu.southwestern.experiment.post.MinecraftBlockRenderExperiment