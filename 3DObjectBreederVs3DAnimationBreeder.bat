set /a id=%1

set /a divnumber=%id%/2
set /a sum=%divnumber%*2

if %id% NEQ %sum% goto odds
if %id% EQU %sum% goto evens

:odds
cd batch
cd Interactive
CALL ThreeDimensionalObjectBreeder-Control.bat %1
cd batch
cd Interactive
CALL ThreeDimensionalAnimationBreeder-Control.bat %1
goto survey

:evens
cd batch
cd Interactive
CALL ThreeDimensionalAnimationBreeder-Control.bat %1
cd batch
cd Interactive
CALL ThreeDimensionalObjectBreeder-Control.bat %1

:survey
start "" https://goo.gl/forms/R41qtcYK8cPeOni02