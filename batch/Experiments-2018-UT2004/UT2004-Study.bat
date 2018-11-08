REM Do not overwrite existing directory
IF exist Subject-%1 goto finished

mkdir Subject-%1
taskkill /F /IM ucc.exe /T
START UT2004-Tutorial.bat %1
START UT2004-JoinLocal.bat
START UT2004-Record.bat Subject-%1\Subject-%1-Tutorial

ECHO "Press a key after your tutorial to go to the first match"
PAUSE

REM get back to starting directory
cd %0\..\

set /a id=%1

set /a divnumber=%id%/2
set /a sum=%divnumber%*2

if %id% NEQ %sum% goto odds
if %id% EQU %sum% goto evens

:odds
taskkill /F /IM ucc.exe /T
START UT2004-Ethan.bat %1
START UT2004-JoinLocal.bat
START UT2004-Record.bat Subject-%1\Subject-%1-EthanMatch-1

ECHO "Press a key after your match to take the survey"
PAUSE
REM Survey 1
START "" https://goo.gl/forms/1xproe3C3HTpPqaG2

ECHO "Press a key after completing the first survey to start your next match"
PAUSE
REM get back to starting directory
cd %0\..\
taskkill /F /IM ucc.exe /T
START UT2004-Jude.bat %1
START UT2004-JoinLocal.bat
START UT2004-Record.bat Subject-%1\Subject-%1-JudeMatch-2 

ECHO "Press a key after your match to take the survey"
PAUSE
REM Survey 2
START "" https://goo.gl/forms/8gQ68KJnMbQk2Ppz2

goto finished

:evens
taskkill /F /IM ucc.exe /T
START UT2004-Jude.bat %1
START UT2004-JoinLocal.bat
START UT2004-Record.bat Subject-%1\Subject-%1-JudeMatch-1

ECHO "Press a key after your match to take the survey"
PAUSE
REM Survey 1
START "" https://goo.gl/forms/1xproe3C3HTpPqaG2

ECHO "Press a key after completing the first survey to start your next match"
PAUSE
REM get back to starting directory
cd %0\..\
taskkill /F /IM ucc.exe /T
START UT2004-Ethan.bat %1
START UT2004-JoinLocal.bat
START UT2004-Record.bat Subject-%1\Subject-%1-EthanMatch-2

ECHO "Press a key after your match to take the survey"
PAUSE
REM Survey 2
START "" https://goo.gl/forms/8gQ68KJnMbQk2Ppz2

:finished
ECHO "DONE"