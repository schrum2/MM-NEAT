REM Usage:   postBestObjectiveWatchTeam.bat <experiment directory> <log prefix> <run type> <run number> <number of trials per team> <teammate.xml> <teammate.xml> <teammate.xml>...
REM Example: postBestObjectiveWatchTeam.bat toruspred TorusPred CoOpMultiCCQ 0 10 gen500_bestIn2.xml gen500_bestIn0.xml gen500_bestIn2.xml
setlocal enabledelayedexpansion
set string=java -jar "target/MM-NEAT-0.0.1-SNAPSHOT.jar" runNumber:%4 parallelEvaluations:false base:%1 log:%2-%3 saveTo:%3 trials:%5 watch:true showNetworks:true io:false netio:false onlyWatchPareto:true printFitness:true animateNetwork:false monitorInputs:true experiment:edu.southwestern.experiment.post.ObjectiveBestTeamsExperiment logLock:true watchLastBestOfTeams:true 
shift
shift
shift
shift
shift
set argCount=0
for %%x in (%*) do set /A argCount+=1
set /a argCount=!argCount!-5
echo "Here is the number of team members:" !argCount!
for /l %%x in (1, 1, !argCount!) do (
set string=!string! coevolvedNet%%x:
set string=!string!%%%%x
)
call !string!