REM Use: AllCombinedDirectAndCPPN.bat <num> <base-dir> <log-prefix>

FOR /L %%i IN (0,1,%1) DO (
  MKDIR "../../%2/Combined%%i"
  python MaxCombine.py ../../%2/CPPN2GAN%%i/%3-CPPN2GAN%%i_MAPElites_log.txt ../../%2/Direct2GAN%%i/%3-Direct2GAN%%i_MAPElites_log.txt ../../%2/Combined%%i/%3-Combined%%i_MAPElites_log.txt
)
