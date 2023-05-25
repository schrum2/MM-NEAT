REM Usage:   postSpawnMinecraftEvaluateBlocks.bat <filename>
REM Example: postSpawnMinecraftEvaluateBlocks.bat minecraftaccumulate/VectorPistonOrientationCount19/archive/NS0EW2UD8_0.07125_7270.txt
java -ea -jar "target/MM-NEAT-0.0.1-SNAPSHOT.jar" minecraftEvaluate minecraftBlockListTextFile:%1 netio:false spaceBetweenMinecraftShapes:10