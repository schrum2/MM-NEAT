REM Use: AllBeatable.bat <num> <base-dir> <log-prefix>

FOR /L %%i IN (0,1,%1) DO (
  python PercentBeatable.py ../../%2/CPPN2GAN%%i/%3-CPPN2GAN%%i_MAPElites_log.txt > ../../%2/CPPN2GAN%%i/%3-CPPN2GAN%%i_PercentBeatable.txt
  python PercentBeatable.py ../../%2/Direct2GAN%%i/%3-Direct2GAN%%i_MAPElites_log.txt > ../../%2/Direct2GAN%%i/%3-Direct2GAN%%i_PercentBeatable.txt 
  python PercentBeatable.py ../../%2/CPPNThenDirect2GAN%%i/%3-CPPNThenDirect2GAN%%i_MAPElites_log.txt > ../../%2/CPPNThenDirect2GAN%%i/%3-CPPNThenDirect2GAN%%i_PercentBeatable.txt 
)
