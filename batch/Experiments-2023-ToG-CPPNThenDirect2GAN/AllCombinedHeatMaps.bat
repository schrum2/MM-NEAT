CALL AllCombinedDirectAndCPPN.bat 29 mariolevelsdecoratensleniency MarioLevelsDecorateNSLeniency
CALL AllCombinedDirectAndCPPN.bat 29 mariolevelsdistinctnsdecorate MarioLevelsDistinctNSDecorate
CALL AllCombinedDirectAndCPPN.bat 29 zeldadungeonsdistinctbtrooms ZeldaDungeonsDistinctBTRooms
CALL AllCombinedDirectAndCPPN.bat 29 zeldadungeonswallwaterrooms ZeldaDungeonsWallWaterRooms

for /D %%f in (../../mariolevelsdecoratensleniency/Combine*) do (
	Rscript.exe MAPElites-Mario-DecorateNSLeniency.R "../../mariolevelsdecoratensleniency/%%f/MarioLevelsDecorateNSLeniency-%%f_MAPElites_log.txt"
)

for /D %%f in (../../mariolevelsdistinctnsdecorate/Combine*) do (
	Rscript.exe MAPElites-Mario-DistinctNSDecorate.R "../../mariolevelsdistinctnsdecorate/%%f/MarioLevelsDistinctNSDecorate-%%f_MAPElites_log.txt"
)

for /D %%f in (../../zeldadungeonsdistinctbtrooms/Combine*) do (
	Rscript.exe MAPElites-Zelda-DistinctBTRooms.R "../../zeldadungeonsdistinctbtrooms/%%f/ZeldaDungeonsDistinctBTRooms-%%f_MAPElites_log.txt"
)

for /D %%f in (../../zeldadungeonswallwaterrooms/Combine*) do (
	Rscript.exe MAPElites-Zelda-WallWaterRooms.R "../../zeldadungeonswallwaterrooms/%%f/ZeldaDungeonsWallWaterRooms-%%f_MAPElites_log.txt"
)
