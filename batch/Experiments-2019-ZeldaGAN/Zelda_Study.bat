@echo off
mkdir Subject-%1

ECHO "Press enter to play an original dungeon..."
pause

START Zelda_PlayOriginal %1

ECHO "Press enter to play a generated dungeon with original rooms..."
pause

START Zelda_PlayGeneratedOriginal %1

ECHO "Press enter to play a generated dungeon with GAN rooms..."
pause
START Zelda_PlayGeneratedGAN %1