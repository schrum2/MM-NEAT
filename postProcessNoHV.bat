REM Usage:   postProcess.bat <experiment directory> <log prefix> <run type> <number of runs>
REM Example: postProcess.bat onelifeconflict OneLifeConflict OneModule 30
java -jar "dist/MM-NEATv2.jar" process:%4 base:%1 log:%2-%3 saveTo:%3 processHV:false
