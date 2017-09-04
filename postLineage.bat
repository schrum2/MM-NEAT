REM Usage:   postLineage.bat <experiment directory> <log prefix> <run type> <run number>
REM Example: postLineage.bat onelifeconflict OneLifeConflict OneModule 0
java -jar "target/MM-NEAT-0.0.1-SNAPSHOT.jar" lineage:%4 base:%1 log:%2-%3 saveTo:%3