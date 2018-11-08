REM Usage: HumanStudy-3DObjectBreederVs3DAnimationBreeder.bat <id number>
REM The id number identifies the subject and determines the order of program presentation

set /a id=%1

set /a divnumber=%id%/2
set /a sum=%divnumber%*2

if %id% NEQ %sum% goto odds
if %id% EQU %sum% goto evens

:odds
CALL ThreeDimensionalObjectBreeder-Control.bat %1
REM Get back to this directory
cd batch
cd Experiments-2018-GECCO-Animation
CALL ThreeDimensionalAnimationBreeder-Control.bat %1
goto survey

:evens
CALL ThreeDimensionalAnimationBreeder-Control.bat %1
REM Get back to this directory
cd batch
cd Experiments-2018-GECCO-Animation
CALL ThreeDimensionalObjectBreeder-Control.bat %1

:survey
start "" https://goo.gl/forms/R41qtcYK8cPeOni02