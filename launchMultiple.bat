REM Usage  : launchMultiple.bat <batch subdir> <batch file> <starting run> <ending run> 
REM example: launchMultiple.bat RLGlue RLGlue-Tetris-ExtendedBT.bat 0 2
REM example: launchMultiple.bat PredPrey TorusPredPrey-TorusPred-CoOpMultiCCQ.bat 1 7
FOR /L %%A IN (%3,1,%4) DO (
  cd batch
  cd %1
  %2 %%A
)
ECHO "All done!"