mkdir Subject-%1
START UT2004-Record.bat Subject-%1\Subject-%1-Tutorial
START UT2004-Tutorial.bat %1
CALL UT2004-JoinLocal.bat

ECHO "Press a key after your tutorial to go to the first match"
PAUSE

set /a id=%1

set /a divnumber=%id%/2
set /a sum=%divnumber%*2

if %id% NEQ %sum% goto odds
if %id% EQU %sum% goto evens

:odds
START UT2004-Record.bat Subject-%1\Subject-%1-Match-1
START UT2004-Ethan.bat %1
CALL UT2004-JoinLocal.bat

ECHO "Press a key after your match to take the survey"
PAUSE
REM Survey 1
START "" https://goo.gl/forms/1xproe3C3HTpPqaG2

ECHO "Press a key after completing the first survey to start your next match"
PAUSE
REM get back to starting directory
cd %0\..\
START UT2004-Record.bat Subject-%1\Subject-%1-Match-2
START UT2004-Jude.bat %1
CALL UT2004-JoinLocal.bat

ECHO "Press a key after your match to take the survey"
PAUSE
REM Survey 2
START "" https://goo.gl/forms/8gQ68KJnMbQk2Ppz2

goto finished

:evens
START UT2004-Record.bat Subject-%1\Subject-%1-Match-1
START UT2004-Jude.bat %1
CALL UT2004-JoinLocal.bat

ECHO "Press a key after your match to take the survey"
PAUSE
REM Survey 1
START "" https://goo.gl/forms/1xproe3C3HTpPqaG2

ECHO "Press a key after completing the first survey to start your next match"
PAUSE
REM get back to starting directory
cd %0\..\
START UT2004-Record.bat Subject-%1\Subject-%1-Match-2
START UT2004-Ethan.bat %1
CALL UT2004-JoinLocal.bat

ECHO "Press a key after your match to take the survey"
PAUSE
REM Survey 2
START "" https://goo.gl/forms/8gQ68KJnMbQk2Ppz2

:finished