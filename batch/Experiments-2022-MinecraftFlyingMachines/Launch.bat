call ResetServerWorld.bat
cd %~dp0
start "CurrentServer" LaunchEvoCraftServer.bat
TIMEOUT /T 60
call %1 %2
taskkill /T /FI "WindowTitle eq CurrentServer"