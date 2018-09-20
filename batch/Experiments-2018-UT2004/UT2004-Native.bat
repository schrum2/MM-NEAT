REM Just launch the server instead? The problem with bots not loading seems to be because of the GameBots game type, but that is not needed for the native bots
REM C:\TEMP\UT2004\System\ucc.exe server DM-Flux2?game=XGame.XTeamGame?fraglimit=0?GoalScore=0?TimeLimit=10?DoUplink=False?UplinkToGamespy=False?SendStats=False?bAllowPrivateChat=False?bAllowTaunts=False?bEnableVoiceChat=False?bAllowLocalBroadcast=False?BotServerPort=3000?ControlServerPort=3001?ObservingServerPort=3002?BlueTeam=XGame.TeamBlueConfigured?RedTeam=XGame.TeamRedConfigured?NumBots=3?MinPlayers=4?QuickStart=True -port=3003


REM Code for INI file (which one?)

REM [UnrealGame.TeamGame]
REM bBalanceTeams=True
REM bPlayersBalanceTeams=True

REM [XGame.TeamRedConfigured]
REM Characters=Zarina

REM [XGame.TeamBlueConfigured]
REM Characters=Zarina
REM Characters=Kragoth

REM Problem: Same person cannot be on both team (problem with Zarina)

cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar utStudyTeammate:native runNumber:%1 randomSeed:%1 base:ut2004 io:true netio:false log:UT2004-Native saveTo:Native numUT2Bots:0 numMirrorBots:0 utNumNativeBots:2 botprizeMod:false utEvalMinutes:10 utNumOpponents:1 utGameType:botTeamGame utMap:DM-Flux2 utBotLogOutput:true utBotKilledAtEnd:false
exit