REM Usage  : launchMultiple.bat <batch subdir> <batch file> <starting run> <ending run> <batch subdir> <batch file> <starting run> <ending run>
REM example: launchMultiple.bat RLGlue RLGlue-CartPole.bat 0 2
REM example: launchMultiple.bat RLGlue RLGlue-CartPole.bat 0 2 PredPrey TorusPredPrey-TorusPred-CoOpMultiCCQ.bat 1 7
REM example: launchMultiple.bat PredPrey TorusPredPrey-TorusPred-CoOpMultiCCQ.bat 0 9 PredPrey TorusPredPrey-TorusPred-CoOpMultiCCQ.bat 10 19
set argCount=0
for %%x in (%*) do set /A argCount+=1
  cd batch
  cd %1
FOR /L %%A IN (%3,1,%4) DO %2 %%A
IF /I %argCount% EQU 8 cd ..
IF /I %argCount% EQU 8 cd %5
IF /I %argCount% EQU 8 FOR /L %%x IN (%7,1,%8) DO %6 %%x
cd ..
cd ..
ECHO "All done!"