for /D %%f in (../../mariolevelsdecoratensleniency/*) do (
	Rscript.exe MAPElites-Mario-DecorateNSLeniency.R "../../mariolevelsdecoratensleniency/%%f/MarioLevelsDecorateNSLeniency-%%f_MAPElites_log.txt"
)
for /D %%f in (../../mariolevelsdecoratensleniency/*Then*) do (
	Rscript.exe MAPElites-Mario-DecorateNSLeniency-CPPNvsDirect.R "../../mariolevelsdecoratensleniency/%%f/MarioLevelsDecorateNSLeniency-%%f_cppnVsDirectFitness_log.txt"
)

for /D %%f in (../../mariolevelsdistinctnsdecorate/*) do (
	Rscript.exe MAPElites-Mario-DistinctNSDecorate.R "../../mariolevelsdistinctnsdecorate/%%f/MarioLevelsDistinctNSDecorate-%%f_MAPElites_log.txt"
)
for /D %%f in (../../mariolevelsdistinctnsdecorate/*Then*) do (
	Rscript.exe MAPElites-Mario-DistinctNSDecorate-CPPNvsDirect.R "../../mariolevelsdistinctnsdecorate/%%f/MarioLevelsDistinctNSDecorate-%%f_cppnVsDirectFitness_log.txt"
)

for /D %%f in (../../zeldadungeonsdistinctbtrooms/*) do (
	Rscript.exe MAPElites-Zelda-DistinctBTRooms.R "../../zeldadungeonsdistinctbtrooms/%%f/ZeldaDungeonsDistinctBTRooms-%%f_MAPElites_log.txt"
)
for /D %%f in (../../zeldadungeonsdistinctbtrooms/*Then*) do (
	Rscript.exe MAPElites-Zelda-DistinctBTRooms-CPPNvsDirect.R "../../zeldadungeonsdistinctbtrooms/%%f/ZeldaDungeonsDistinctBTRooms-%%f_cppnVsDirectFitness_log.txt"
)

for /D %%f in (../../zeldadungeonswallwaterrooms/*) do (
	Rscript.exe MAPElites-Zelda-WallWaterRooms.R "../../zeldadungeonswallwaterrooms/%%f/ZeldaDungeonsWallWaterRooms-%%f_MAPElites_log.txt"
)
for /D %%f in (../../zeldadungeonswallwaterrooms/*Then*) do (
	Rscript.exe MAPElites-Zelda-WallWaterRooms-CPPNvsDirect.R "../../zeldadungeonswallwaterrooms/%%f/ZeldaDungeonsWallWaterRooms-%%f_cppnVsDirectFitness_log.txt"
)
