cd ..
cd ..
java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:ttt trials:10 maxGens:500 mu:100 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.boardGame.BoardGameTask cleanOldNetworks:true fs:false log:TicTacToe-Random saveTo:Random boardGame:boardGame.ttt.TicTacToe