call ResetServerWorld.bat
cd %~dp0
start LaunchEvoCraftServer.bat
TIMEOUT /T 100
call %1 %2