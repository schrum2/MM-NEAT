REM Usage:   postLineage.bat <experiment directory> <log prefix> <run type> <run number>
REM Example: postLineage.bat onelifeconflict OneLifeConflict OneModule 0
java -jar "dist/MM-NEATv2.jar" lineage:%4 base:%1 log:%2-%3 saveTo:%3