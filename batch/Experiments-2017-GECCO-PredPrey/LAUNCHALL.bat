REM Not the most efficient way to run all of these experiments, but it will work.
REM Better to run batches of runs of the same type in parellel using launchMultipleAsynchronously.bat
cd ..
cd ..
REM split sensors
start launchMultiple.bat Experiments-2017-GECCO-PredPrey TorusPredPrey-Team1M.bat 0 29
start launchMultiple.bat Experiments-2017-GECCO-PredPrey TorusPredPrey-Team2M.bat 0 29
start launchMultiple.bat Experiments-2017-GECCO-PredPrey TorusPredPrey-Individual1M.bat 0 29
start launchMultiple.bat Experiments-2017-GECCO-PredPrey TorusPredPrey-Individual2M.bat 0 29
start launchMultiple.bat Experiments-2017-GECCO-PredPrey TorusPredPrey-Both1M.bat 0 29
start launchMultiple.bat Experiments-2017-GECCO-PredPrey TorusPredPrey-Both2M.bat 0 29
