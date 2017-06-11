REM Not the most efficient way to run all of these experiments, but it will work.
REM Better to run batches of runs of the same type in parellel using launchMultipleAsynchronously.bat
cd ..
cd ..
start launchMultiple.bat Experiments-GECCO-2014-MsPacMan MsPacMan-OneLifeConflict-OneModule.bat 0 19
start launchMultiple.bat Experiments-GECCO-2014-MsPacMan MsPacMan-OneLifeConflict-TwoModules.bat 0 19
start launchMultiple.bat Experiments-GECCO-2014-MsPacMan MsPacMan-OneLifeConflict-ThreeModules.bat 0 19
start launchMultiple.bat Experiments-GECCO-2014-MsPacMan MsPacMan-OneLifeConflict-MMD.bat 0 19
