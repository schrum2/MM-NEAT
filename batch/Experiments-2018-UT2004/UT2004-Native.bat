cd ..
cd ..
set /p texte=< my_ut2004_path.txt  
cd %texte%
cd System
ucc.exe server DM-Flux2?game=XGame.XTeamGame?fraglimit=0?GoalScore=0?TimeLimit=10?DoUplink=False?UplinkToGamespy=False?SendStats=False?bAllowPrivateChat=False?bAllowTaunts=False?bEnableVoiceChat=False?bAllowLocalBroadcast=False?BotServerPort=3000?ControlServerPort=3001?ObservingServerPort=3002?RedTeam=XGame.TeamRedConfigured?BlueTeam=XGame.TeamBlueConfigured?NumBots=3?MinPlayers=4?QuickStart=False -port=3003

REM The approach above assumes the following configurations are in the appropriate INI file

REM [UnrealGame.TeamGame]
REM bBalanceTeams=True
REM bPlayersBalanceTeams=True

REM [XGame.TeamRedConfigured]
REM Characters=Zarina

REM [XGame.TeamBlueConfigured]
REM Characters=Greith
REM Characters=Kragoth

REM Launch MM-NEAT to launch game with native bots and GameBots mod
REM java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar utStudyTeammate:native runNumber:%1 randomSeed:%1 base:ut2004 io:true netio:false log:UT2004-Native saveTo:Native numUT2Bots:0 numMirrorBots:0 utNumNativeBots:2 botprizeMod:false utEvalMinutes:10 utNumOpponents:1 utGameType:botTeamGame utMap:DM-Flux2 utBotLogOutput:true utBotKilledAtEnd:false
exit