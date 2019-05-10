REM Not the most efficient way to run all of these experiments, but it will work.
REM Better to run batches of runs of the same type in parellel using launchMultipleAsynchronously.bat
cd ..
cd ..
REM Full observability ghosts
start launchMultiple.bat Experiments-2019-CEC-POMsPacMan MsPacManPO-COGhosts-1M.bat 0 19
start launchMultiple.bat Experiments-2019-CEC-POMsPacMan MsPacManPO-COGhosts-2MPref.bat 0 19
start launchMultiple.bat Experiments-2019-CEC-POMsPacMan MsPacManPO-COGhosts-3MMultitask.bat 0 19
start launchMultiple.bat Experiments-2019-CEC-POMsPacMan MsPacManPO-COGhosts-3MPref.bat 0 19
start launchMultiple.bat Experiments-2019-CEC-POMsPacMan MsPacManPO-COGhosts-MMD.bat 0 19
REM Partial observability ghosts
start launchMultiple.bat Experiments-2019-CEC-POMsPacMan MsPacManPO-POGhosts-1M.bat 0 19
start launchMultiple.bat Experiments-2019-CEC-POMsPacMan MsPacManPO-POGhosts-2MPref.bat 0 19
start launchMultiple.bat Experiments-2019-CEC-POMsPacMan MsPacManPO-POGhosts-3MMultitask.bat 0 19
start launchMultiple.bat Experiments-2019-CEC-POMsPacMan MsPacManPO-POGhosts-3MPref.bat 0 19
start launchMultiple.bat Experiments-2019-CEC-POMsPacMan MsPacManPO-POGhosts-MMD.bat 0 19

