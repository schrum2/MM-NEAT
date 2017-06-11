REM Not the most efficient way to run all of these experiments, but it will work.
REM Better to run batches of runs of the same type in parellel using launchMultipleAsynchronously.bat
cd ..
cd ..
REM split sensors
start launchMultiple.bat Experiments-2016-TCIAIG-MsPacMan MsPacMan-OneLifeSplit-OneModule.bat 0 29
start launchMultiple.bat Experiments-2016-TCIAIG-MsPacMan MsPacMan-OneLifeSplit-TwoModules.bat 0 29
start launchMultiple.bat Experiments-2016-TCIAIG-MsPacMan MsPacMan-OneLifeSplit-ThreeModules.bat 0 29
start launchMultiple.bat Experiments-2016-TCIAIG-MsPacMan MsPacMan-OneLifeSplit-TwoModuleMultitask.bat 0 29
start launchMultiple.bat Experiments-2016-TCIAIG-MsPacMan MsPacMan-OneLifeSplit-ThreeModuleMultitask.bat 0 29
start launchMultiple.bat Experiments-2016-TCIAIG-MsPacMan MsPacMan-OneLifeSplit-MMD.bat 0 29
start launchMultiple.bat Experiments-2016-TCIAIG-MsPacMan MsPacMan-OneLifeSplit-MMP.bat 0 29
start launchMultiple.bat Experiments-2016-TCIAIG-MsPacMan MsPacMan-OneLifeSplit-MMR.bat 0 29
start launchMultiple.bat Experiments-2016-TCIAIG-MsPacMan MsPacMan-OneLifeSplit-MMAll.bat 0 29
REM conflict sensors
start launchMultiple.bat Experiments-2016-TCIAIG-MsPacMan MsPacMan-OneLifeConflict-OneModule.bat 0 29
start launchMultiple.bat Experiments-2016-TCIAIG-MsPacMan MsPacMan-OneLifeConflict-TwoModules.bat 0 29
start launchMultiple.bat Experiments-2016-TCIAIG-MsPacMan MsPacMan-OneLifeConflict-ThreeModules.bat 0 29
start launchMultiple.bat Experiments-2016-TCIAIG-MsPacMan MsPacMan-OneLifeConflict-TwoModuleMultitask.bat 0 29
start launchMultiple.bat Experiments-2016-TCIAIG-MsPacMan MsPacMan-OneLifeConflict-ThreeModuleMultitask.bat 0 29
start launchMultiple.bat Experiments-2016-TCIAIG-MsPacMan MsPacMan-OneLifeConflict-MMD.bat 0 29
start launchMultiple.bat Experiments-2016-TCIAIG-MsPacMan MsPacMan-OneLifeConflict-MMP.bat 0 29
start launchMultiple.bat Experiments-2016-TCIAIG-MsPacMan MsPacMan-OneLifeConflict-MMR.bat 0 29
start launchMultiple.bat Experiments-2016-TCIAIG-MsPacMan MsPacMan-OneLifeConflict-MMAll.bat 0 29

