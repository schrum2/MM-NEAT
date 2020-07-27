for /D %%f in (../../loderunnermapelites/*) do (
    Rscript.exe MAPElites-LodeRunner.R ../../loderunnermapelites/%%f/LodeRunnerMAPElites-%%f_MAPElites_log.txt
)