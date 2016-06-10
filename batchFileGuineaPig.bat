REM Usage  : batchFileGuineaPig.bat <favorite food> <color> <number> <food> <food> <food> ...
REM example: batchFileGuineaPig.bat Hamburgers Brown 1023429 apple chips banana candy
REM Note: Cannot provide more than 6 foods (no more than 10 total arguments)
@echo off
setlocal enabledelayedexpansion
set argCount=0
for %%x in (%*) do set /A argCount+=1
echo Number of processed arguments: %argCount%
set string=%4
for /l %%x in (5, 1, %argCount%) do (
set string=!string! %%%%x
)
call echo !string!