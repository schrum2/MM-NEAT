cd ..
cd ..
set /p texte=< my_ut2004_path.txt  
cd %texte%
cd System
C:\WINDOWS\system32\cmd.exe /c start "UT2004" /belownormal "UT2004.exe" "127.0.0.1"