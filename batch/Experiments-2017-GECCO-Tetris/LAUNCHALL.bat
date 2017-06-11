REM Not the most efficient way to run all of these experiments, but it will work.
REM Better to run batches of runs of the same type in parellel using launchMultipleAsynchronously.bat
cd ..
cd ..
REM split sensors
start launchMultiple.bat Experiments-2017-GECCO-Tetris RLGlue-Tetris-PlainRaw.bat 0 29
start launchMultiple.bat Experiments-2017-GECCO-Tetris RLGlue-Tetris-PlainFeatures.bat 0 29
start launchMultiple.bat Experiments-2017-GECCO-Tetris RLGlue-Tetris-HyperNEATRaw.bat 0 29
start launchMultiple.bat Experiments-2017-GECCO-Tetris RLGlue-Tetris-HyperNEATFeatures.bat 0 29
