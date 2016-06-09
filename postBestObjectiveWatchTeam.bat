REM Usage:   postBestObjectiveWatchTeam.bat <experiment directory> <log prefix> <run type> <run number> <number of trials per team> <teammate.xml> <teammate.xml> <teammate.xml>...
REM Example: postBestObjectiveWatchTeam.bat toruspred TorusPred CoOpMultiCCQ 0 10 gen500_bestIn2.xml gen500_bestIn0.xml gen500_bestIn2.xml
REM Note: Cannot provide more than 4 team members (no more than 10 total arguments)
setlocal enabledelayedexpansion
set string=java -jar "dist/MM-NEATv2.jar" runNumber:%4 parallelEvaluations:false base:%1 log:%2-%3 saveTo:%3 trials:%5 watch:true showNetworks:true io:false netio:false onlyWatchPareto:true printFitness:true animateNetwork:false ucb1Evaluation:false showSubnetAnalysis:true monitorInputs:true experiment:edu.utexas.cs.nn.experiment.ObjectiveBestTeamsExperiment logLock:true watchLastBestOfTeams:true rlGluePort:4200
set argCount=0
for %%x in (%*) do set /A argCount+=1
set counter=0
for /l %%x in (6, 1, %argCount%) do (
set string=!string! coevolvedNet!counter!:
set string=!string!%%%%x
set /a counter=!counter!+1
)
call !string!