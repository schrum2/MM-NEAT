REM python 2DMAPElitesArchivePlottAnimator.py <plot file to display> <first dimension name> <first dimension size> <second dimension name> <second dimension size> <max value> <min value>
REM The min and max values are not required, and instead will be calculated automatically
cd ..
cd ..
C:\Users\raffertyt\AppData\Local\anaconda3\python.exe 2DMAPElitesArchivePlotAnimator.py minecraftinteger/MEObserverIntegerCountNegative1/MinecraftInteger-MEObserverIntegerCountNegative1_MAPElites_log.txt "MinecraftInteger-MEObserverIntegerCountNegative1_MAPElites" "OccupiedCountFitness" 27 "NegativeSpaceCountFitness" 26 %1 %2 %3 %4