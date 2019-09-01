@echo off

set /a id=%4

ECHO "Press enter to play 1st dungeon..."
pause
START /wait /MAX %1 %id%
wscript StopRecord.vbs
ECHO "Please get the attention of the researcher overseeing your session..."
pause
ECHO "Press enter to take survey..."
pause
START Survey1
echo;
ECHO "Press enter to play 2nd dungeon..."
pause
START /wait /MAX %2 %id%
wscript StopRecord.vbs
ECHO "Please get the attention of the researcher overseeing your session..."
pause
ECHO "Press enter to take survey..."
pause
START Survey2
echo;
ECHO "Press enter to play 3rd dungeon..."
pause
START /wait /MAX %3 %id%
wscript StopRecord.vbs
ECHO "Please get the attention of the researcher overseeing your session..."
pause
ECHO "Press enter to take survey..."
pause
START Survey3

exit