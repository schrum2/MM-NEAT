@echo off

set /a id=%1

mkdir Subject-%id%

FOR %%G IN (0 6 12 18 24) DO (
    IF %%G EQU %id% GOTO order1
)

FOR %%G IN (1 7 13 19 25) DO (
    IF %%G EQU %id% GOTO order2
)

FOR %%G IN (2 8 14 20 26) DO (
    IF %%G EQU %id% GOTO order3
)

FOR %%G IN (3 9 15 21 27) DO (
    IF %%G EQU %id% GOTO order4
)

FOR %%G IN (4 10 16 22 28) DO (
    IF %%G EQU %id% GOTO order5
)

FOR %%G IN (5 11 17 23 29) DO (
    IF %%G EQU %id% GOTO order6
)

ECHO "Picked an illegal ID number! Only 0-29 allowed"
GOTO endstudy

:order1
START /MAX Sequence Zelda_PlayOriginal Zelda_PlayGeneratedOriginal Zelda_PlayGeneratedGAN %id%
GOTO endstudy

:order2
START /MAX Sequence Zelda_PlayOriginal Zelda_PlayGeneratedGAN Zelda_PlayGeneratedOriginal %id%
GOTO endstudy

:order3
START /MAX Sequence Zelda_PlayGeneratedOriginal Zelda_PlayOriginal Zelda_PlayGeneratedGAN %id%
GOTO endstudy

:order4
START /MAX Sequence Zelda_PlayGeneratedOriginal Zelda_PlayGeneratedGAN Zelda_PlayOriginal %id%
GOTO endstudy

:order5
START /MAX Sequence Zelda_PlayGeneratedGAN Zelda_PlayOriginal Zelda_PlayGeneratedOriginal %id%
GOTO endstudy

:order6
START /MAX Sequence Zelda_PlayGeneratedGAN Zelda_PlayGeneratedOriginal Zelda_PlayOriginal %id%
GOTO endstudy

:endstudy
