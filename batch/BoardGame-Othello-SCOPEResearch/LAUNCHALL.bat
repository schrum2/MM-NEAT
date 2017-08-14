REM Not the most efficient way to run all of these experiments, but it will work.
REM Better to run batches of runs of the same type in parellel using launchMultipleAsynchronously.bat
cd ..
cd ..
REM split sensors
start launchMultipleAsynchronously.bat BoardGame-Othello-SCOPEResearch Othello-NEATStaticWPC05Second.bat 0 9
