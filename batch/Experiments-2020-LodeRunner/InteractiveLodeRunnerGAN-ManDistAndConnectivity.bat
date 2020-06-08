{\rtf1\ansi\ansicpg1252\cocoartf2512
\cocoatextscaling0\cocoaplatform0{\fonttbl\f0\fswiss\fcharset0 Helvetica;}
{\colortbl;\red255\green255\blue255;\red0\green0\blue0;}
{\*\expandedcolortbl;;\cssrgb\c0\c0\c0;}
\margl1440\margr1440\vieww10800\viewh8400\viewkind0
\pard\tx720\tx1440\tx2160\tx2880\tx3600\tx4320\tx5040\tx5760\tx6480\tx7200\tx7920\tx8640\pardirnatural\partightenfactor0

\f0\fs24 \cf2 cd ..\
cd ..\
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:0 randomSeed:0 base:loderunnerlevels log:LodeRunnerLevels-Direct saveTo:Direct LodeRunnerGANModel:LodeRunnerEpochAllGroundFirstHundred100000_20_7.pth watch:true GANInputSize:20 trials:1 mu:100 maxGens:100000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.loderunner.LodeRunnerGANLevelTask cleanFrequency:-1 saveAllChampions:true, cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false watch:false steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000}