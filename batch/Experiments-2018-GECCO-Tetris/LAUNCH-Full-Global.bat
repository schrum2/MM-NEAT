REM Not the most efficient way to run all of these experiments, but it will work.
REM Better to run batches of runs of the same type in parellel using launchMultipleAsynchronously.bat
cd ..
cd ..
REM Full Global THRESH
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-SubCoordsFullDepth1Width1Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-SubCoordsFullDepth1Width4Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-SubCoordsFullDepth4Width4None.bat 0 9
REM Full Global LEO
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-LEOSubCoordsFullDepth1Width1Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-LEOSubCoordsFullDepth1Width4Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-LEOSubCoordsFullDepth4Width4None.bat 0 9

