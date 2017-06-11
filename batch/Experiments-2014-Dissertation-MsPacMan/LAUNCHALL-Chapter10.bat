REM Not the most efficient way to run all of these experiments, but it will work.
REM Better to run batches of runs of the same type in parellel using launchMultipleAsynchronously.bat
cd ..
cd ..
REM One life with split sensors
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-OneLifeSplit-OneModule.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-OneLifeSplit-TwoModules.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-OneLifeSplit-ThreeModules.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-OneLifeSplit-TwoModuleMultitask.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-OneLifeSplit-ThreeModuleMultitask.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-OneLifeSplit-MMD.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-OneLifeSplit-MMR.bat 0 19
REM One life with conflict sensors
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-OneLifeConflict-OneModule.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-OneLifeConflict-TwoModules.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-OneLifeConflict-ThreeModules.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-OneLifeConflict-TwoModuleMultitask.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-OneLifeConflict-ThreeModuleMultitask.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-OneLifeConflict-MMD.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-OneLifeConflict-MMR.bat 0 19
REM Multiple lives without TUG
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-MultipleLivesConflict-OneModule.bat 0 9
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-MultipleLivesConflict-TwoModules.bat 0 9
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-MultipleLivesConflict-ThreeModules.bat 0 9
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-MultipleLivesConflict-MMD.bat 0 9
REM Multiple lives with TUG
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-MultipleLivesConflict-TUGOneModule.bat 0 9
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-MultipleLivesConflict-TUGTwoModules.bat 0 9
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-MultipleLivesConflict-TUGThreeModules.bat 0 9
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-MultipleLivesConflict-TUGMMD.bat 0 9
