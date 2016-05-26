REM Usage  : launchMultiple.bat <batch subdir> <batch file> <num runs>
REM example: launchMultiple.bat RLGlue RLGlue-CartPole.bat 3
set /a "last=%3-1"
FOR /L %%A IN (0,1,%last%) DO (
  cd batch
  cd %1
  %2 %%A
)
ECHO "All done!"