REM Not the most efficient way to run all of these experiments, but it will work.
REM Better to run batches of runs of the same type in parellel using launchMultipleAsynchronously.bat
cd ..
cd ..
REM split sensors
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-ImprisonSplit-OneModule.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-ImprisonSplit-TwoModules.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-ImprisonSplit-ThreeModules.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-ImprisonSplit-TwoModuleMultitask.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-ImprisonSplit-ThreeModuleMultitask.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-ImprisonSplit-MMD.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-ImprisonSplit-MMR.bat 0 19
REM conflict sensors
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-ImprisonConflict-OneModule.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-ImprisonConflict-TwoModules.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-ImprisonConflict-ThreeModules.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-ImprisonConflict-TwoModuleMultitask.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-ImprisonConflict-ThreeModuleMultitask.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-ImprisonConflict-MMD.bat 0 19
start launchMultiple.bat Experiments-2014-Dissertation-MsPacMan MsPacMan-ImprisonConflict-MMR.bat 0 19

