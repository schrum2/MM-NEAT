REM Not the most efficient way to run all of these experiments, but it will work.
REM Better to run batches of runs of the same type in parellel using launchMultipleAsynchronously.bat
cd ..
cd ..
REM Full MSS THRESH
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-FullDepth1Width1Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-FullDepth1Width4Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-FullDepth4Width4None.bat 0 9
REM Full MSS LEO
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-LEOFullDepth1Width1Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-LEOFullDepth1Width4Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-LEOFullDepth4Width4None.bat 0 9
