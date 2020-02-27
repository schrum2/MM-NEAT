REM Not the most efficient way to run all of these experiments, but it will work.
REM Better to run batches of runs of the same type in parellel using launchMultipleAsynchronously.bat
cd ..
cd ..
REM Automated Zentangle Experiments
start launchMultiple.bat Experiments-2020-EvoMUSART-Zentangles Launch-Zentangle-RandomColor.bat 0 4
start launchMultiple.bat Experiments-2020-EvoMUSART-Zentangles Launch-Zentangle-RandomBW.bat 0 4
start launchMultiple.bat Experiments-2020-EvoMUSART-Zentangles Launch-Zentangle-HB3Color.bat 0 4
start launchMultiple.bat Experiments-2020-EvoMUSART-Zentangles Launch-Zentangle-HB.bat 0 4
