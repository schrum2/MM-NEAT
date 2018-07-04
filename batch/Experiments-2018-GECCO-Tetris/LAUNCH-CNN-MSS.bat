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
