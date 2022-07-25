call ResetServerWorld.bat
cd %~dp0
start "CurrentServer" LaunchEvoCraftServer.bat
TIMEOUT /T 60
call %1 %2
REM Kill the server
wmic process where "commandline like '%%java%%sponge%%'" delete