REM Usage: HumanStudy-PicbreederVsAnimationBreeder.bat <id number>
REM The id number identifies the subject and determines the order of program presentation

set /a id=%1

set /a divnumber=%id%/2
set /a sum=%divnumber%*2

if %id% NEQ %sum% goto odds
if %id% EQU %sum% goto evens

:odds
CALL Picbreeder-Control.bat %1
REM Get back to this directory
cd batch
cd Experiments-2018-GECCO-Animation
CALL AnimationBreeder-Control.bat %1
goto survey

:evens
CALL AnimationBreeder-Control.bat %1
REM Get back to this directory
cd batch
cd Experiments-2018-GECCO-Animation
CALL Picbreeder-Control.bat %1
:survey

start "" https://goo.gl/forms/Gm6n5ug0OBAziLX73