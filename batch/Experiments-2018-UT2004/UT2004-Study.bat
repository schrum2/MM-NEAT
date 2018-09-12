set /a id=%1

set /a divnumber=%id%/2
set /a sum=%divnumber%*2

if %id% NEQ %sum% goto odds
if %id% EQU %sum% goto evens

:odds
START UT2004-Ethan.bat %1
CALL UT2004-JoinLocal.bat

REM Survey 1
CALL "" https://goo.gl/forms/1xproe3C3HTpPqaG2

REM get back to starting directory
cd %0\..\
START UT2004-Jude.bat %1
CALL UT2004-JoinLocal.bat

REM Survey 2
CALL "" https://goo.gl/forms/8gQ68KJnMbQk2Ppz2

goto finished

:evens
START UT2004-Jude.bat %1
CALL UT2004-JoinLocal.bat

REM Survey 1
CALL "" https://goo.gl/forms/1xproe3C3HTpPqaG2

REM get back to starting directory
cd %0\..\
START UT2004-Ethan.bat %1
CALL UT2004-JoinLocal.bat

REM Survey 2
CALL "" https://goo.gl/forms/8gQ68KJnMbQk2Ppz2

:finished