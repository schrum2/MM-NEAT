REM Not the most efficient way to run all of these experiments, but it will work.
REM Better to run batches of runs of the same type in parellel using launchMultipleAsynchronously.bat
cd ..
cd ..
REM CNN Global THRESH
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-SubCoordsConvolutionDepth1Width1Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-SubCoordsConvolutionDepth1Width4Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-SubCoordsConvolutionDepth4Width4None.bat 0 9
REM CNN Global LEO
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-LEOSubCoordsConvolutionDepth1Width1Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-LEOSubCoordsConvolutionDepth1Width4Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-LEOSubCoordsConvolutionDepth4Width4None.bat 0 9
