REM Usage  : launchMultipleAsynchronously.bat <batch subdir> <batch file> <starting run> <ending run> <batch subdir> <batch file> <starting run> <ending run>
REM example: launchMultipleAsynchronously.bat RLGlue RLGlue-Tetris-ExtendedBT.bat 0 2
REM example: launchMultipleAsynchronously.bat RLGlue RLGlue-Tetris-ExtendedBT.bat 0 2 PredPrey TorusPredPrey-TorusPred-CoOpMultiCCQ.bat 1 3
REM example: launchMultipleAsynchronously.bat PredPrey TorusPredPrey-TorusPred-CoOpMultiCCQ.bat 0 2 PredPrey TorusPredPrey-TorusPred-CoOpMultiCCQ.bat 4 5
setlocal enabledelayedexpansion
set argCount=0
for %%x in (%*) do set /A argCount+=1
  cd batch
  cd %1
FOR /L %%A IN (%3,1,%4) DO start %2 %%A
IF /I %argCount% EQU 8 cd ..
IF /I %argCount% EQU 8 cd %5
IF /I %argCount% EQU 8 FOR /L %%x IN (%7,1,%8) DO start %6 %%x
cd ..
cd ..
ECHO "All done!"