REM Not the most efficient way to run all of these experiments, but it will work.
REM Better to run batches of runs of the same type in parellel using launchMultipleAsynchronously.bat
cd ..
cd ..
REM CNN MSS THRESH
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-ConvolutionDepth1Width1Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-ConvolutionDepth1Width4Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-ConvolutionDepth4Width4None.bat 0 9
REM CNN MSS LEO
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-LEOConvolutionDepth1Width1Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-LEOConvolutionDepth1Width4Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-LEOConvolutionDepth4Width4None.bat 0 9
REM CNN Global THRESH
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-SubCoordsConvolutionDepth1Width1Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-SubCoordsConvolutionDepth1Width4Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-SubCoordsConvolutionDepth4Width4None.bat 0 9
REM CNN Global LEO
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-LEOSubCoordsConvolutionDepth1Width1Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-LEOSubCoordsConvolutionDepth1Width4Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-LEOSubCoordsConvolutionDepth4Width4None.bat 0 9
REM Full MSS THRESH
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-FullDepth1Width1Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-FullDepth1Width4Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-FullDepth4Width4None.bat 0 9
REM Full MSS LEO
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-LEOFullDepth1Width1Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-LEOFullDepth1Width4Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-LEOFullDepth4Width4None.bat 0 9
REM Full Global THRESH
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-SubCoordsFullDepth1Width1Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-SubCoordsFullDepth1Width4Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-SubCoordsFullDepth4Width4None.bat 0 9
REM Full Global LEO
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-LEOSubCoordsFullDepth1Width1Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-LEOSubCoordsFullDepth1Width4Extra.bat 0 29
start launchMultiple.bat Experiments-2018-GECCO-Tetris Tetris-HyperNEAT-LEOSubCoordsFullDepth4Width4None.bat 0 9

