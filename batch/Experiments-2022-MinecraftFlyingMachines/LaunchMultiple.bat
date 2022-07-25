REM Usage  : LaunchMultiple.bat <batch file> <starting run> <ending run> 
REM example: launchMultiple.bat MinecraftAccumulate-ME-Observer-VectorCountNegative.bat 0 2
FOR /L %%A IN (%2,1,%3) DO (
  call Launch.bat %1 %%A
  cd %~dp0
)
ECHO "All done!"