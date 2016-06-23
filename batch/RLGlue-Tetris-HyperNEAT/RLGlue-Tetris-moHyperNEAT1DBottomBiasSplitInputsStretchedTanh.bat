cd ..
cd ..
setlocal enabledelayedexpansion
set /a r=%1
set string=!string! rlGluePort:
set string=!string!!r!
call !string!
