REM python 3DMAPElitesArchivePlotAnimator.py <plot file to display> <first dimension name> <first dimension size> <second dimension name> <second dimension size> <third dimension name> <third dimension size> <row amount> <max value> <min value>
REM The min and max values are not required, and instead will be calculated automatically
cd ..
cd ..
C:\Users\raffertyt\AppData\Local\anaconda3\python.exe 3DMAPElitesArchivePlotAnimator.py missileminecraft/DirectedBiggerVectorPistonOrientation8/MissileMinecraft-DirectedBiggerVectorPistonOrientation8_MAPElites_log.txt "MissileMinecraft-DirectedBiggerVectorPistonOrientation8_MAPElites" "NorthSouthPistonCountFitness" 5 "UpDownPistonCountFitness" 5 "EastWestPistonCountFitness" 5 2 %1 %2 %3