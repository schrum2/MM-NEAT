cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar utStudyTeammate:none runNumber:%1 randomSeed:%1 base:ut2004 io:true netio:false log:UT2004-None saveTo:None numUT2Bots:0 numMirrorBots:0 utNumNativeBots:1 botprizeMod:false utEvalMinutes:5 utNumOpponents:1 utGameType:botTeamGame utMap:DM-Flux2 utBotLogOutput:true utBotKilledAtEnd:false > batch/Experiments-2018-UT2004/Subject-%1/Subject-%1-TutorialMatch.txt
exit