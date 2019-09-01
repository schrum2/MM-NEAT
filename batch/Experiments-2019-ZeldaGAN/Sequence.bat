@echo off

set /a id=%4

ECHO "Press enter to play 1st dungeon..."
pause
START /MAX %1 %id%

ECHO "Press enter to take survey..."
pause
START Survey1
echo;

ECHO "Press enter to play 2nd dungeon..."
pause
START /MAX %2 %id%

ECHO "Press enter to take survey..."
pause
START Survey2

echo;
ECHO "Press enter to play 3rd dungeon..."
pause
START /MAX %3 %id%

ECHO "Press enter to take survey..."
pause
START Survey3

exit