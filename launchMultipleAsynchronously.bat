REM Usage  : launchMultipleAsynchronously.bat <batch subdir> <batch file> <starting run> <ending run>
REM example: launchMultipleAsynchronously.bat RLGlue RLGlue-Tetris-ExtendedBT.bat 0 2
REM example: launchMultipleAsynchronously.bat PredPrey TorusPredPrey-TorusPred-CoOpMultiCCQ.bat 1 3
cd batch
cd %1
FOR /L %%A IN (%3,1,%4) DO (
  start %2 %%A
)
ECHO "All done!"