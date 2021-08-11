#!/usr/bin/env Rscript

# Possible to set "generation"
args = commandArgs(trailingOnly=TRUE)

setwd("../../mariolevelsdecoratensleniency")
#setwd("G:\\My Drive\\Research\\2021-IEEE-ToG-CPPNThenDirectToGAN\\MM-NEAT\\mariolevelsdecoratensleniency")
print("Load data")
types <- list("CPPN2GAN","CPPNThenDirect2GAN","Direct2GAN")

allData = array()

type <- 0
for(typePrefix in types) {
#typePrefix <- "CPPN2GAN"

for(i in 0:29) {
  dataFile <- paste(typePrefix,i,"/MarioLevelsDecorateNSLeniency-",typePrefix,i,"_MAPElites_log.txt",sep="")
  map <- read.table(dataFile, na.strings = c("X"))
  print(dataFile)
  if (length(args)==0) {
    # Only the final archive matters
    lastRow <- map[map$V1 == nrow(map) - 1, ]
    nameEnd <- "LAST"
  } else {
    lastRow <- map[map$V1 == strtoi(args[1], base = 0L) - 1, ]
    nameEnd <- paste("Gen",args[1],sep="")
  }
  lastRow[ is.na(lastRow) ] <- -Inf
  archive <- data.frame(matrix(unlist(lastRow[2:length(lastRow)]), nrow=(length(lastRow)-1), byrow=T))
  names(archive) <- "SolutionSteps"
  
  # Change -Infinity to 0
  archive[archive<0] <- 0
  
  if(i > 0) {
    print("Add")
    averageArchive <- averageArchive + archive
  } else {
    print("Start")
    averageArchive <- archive
  }
}

archive <- averageArchive / 30

# Add data indicating how the data is binned, based on convention of how
# output data is organized

print("Organize bins")

decorationBin <- append(rep(0, 10*10), rep(1, 10*10))
decorationBin <- append(decorationBin, rep(2, 10*10))
decorationBin <- append(decorationBin, rep(3, 10*10))
decorationBin <- append(decorationBin, rep(4, 10*10))
decorationBin <- append(decorationBin, rep(5, 10*10))
decorationBin <- append(decorationBin, rep(6, 10*10))
decorationBin <- append(decorationBin, rep(7, 10*10))
decorationBin <- append(decorationBin, rep(8, 10*10))
decorationBin <- append(decorationBin, rep(9, 10*10))

decorationBin <- data.frame(decorationBin)

nsBin <- append(rep(0, 10), rep(1, 10))
nsBin <- append(nsBin, rep(2, 10))
nsBin <- append(nsBin, rep(3, 10))
nsBin <- append(nsBin, rep(4, 10))
nsBin <- append(nsBin, rep(5, 10))
nsBin <- append(nsBin, rep(6, 10))
nsBin <- append(nsBin, rep(7, 10))
nsBin <- append(nsBin, rep(8, 10))
nsBin <- append(nsBin, rep(9, 10))

nsBin <- rep(nsBin, 10)
nsBin <- data.frame(nsBin)

leniencyBin <- rep(seq(-5,4),10*10)
leniencyBin <- data.frame(leniencyBin)

typeBin <- rep(type, 1000)
typeBin <- data.frame(typeBin)

temp <- data.frame(archive, typeBin, decorationBin, nsBin, leniencyBin)
if(exists("allData")) {
  allData <- rbind(allData,temp)
} else {
  allData <- temp
}

type <- type + 1

}

### Remove the NA row

allData <- allData[complete.cases(allData), ]

###############################################

library(ggplot2)
library(dplyr)
library(viridis)
library(stringr)
library(scales)

print("Create plot and save to file")

leniencyLabals <- function(num) {
  paste("Leniency Bin:",num)
}

# Fill with data on overlaping bins
overlapData = NULL
numBins <- 1000
for(i in 1:numBins) { # 1000 bins
  newRow <- allData[i, ] # CPPN2GAN
  ThenRow <- allData[i+numBins, ] #CPPNThenDirect2GAN
  DRow <- allData[i+2*numBins, ] #Direct2GAN
  
  if(newRow$SolutionSteps > ThenRow$SolutionSteps & newRow$SolutionSteps > DRow$SolutionSteps) {
    newRow$SolutionSteps <- "CPPN2GAN"
  } else if(ThenRow$SolutionSteps > newRow$SolutionSteps & ThenRow$SolutionSteps > DRow$SolutionSteps) {
    newRow$SolutionSteps <- "CPPNThenDirect2GAN"
  } else if(DRow$SolutionSteps > newRow$SolutionSteps & DRow$SolutionSteps > ThenRow$SolutionSteps) {
    newRow$SolutionSteps <- "Direct2GAN"
  } else if(newRow$SolutionSteps == ThenRow$SolutionSteps & newRow$SolutionSteps > DRow$SolutionSteps) {
    newRow$SolutionSteps <- "CPPN Tie"
  } else if(DRow$SolutionSteps == ThenRow$SolutionSteps & DRow$SolutionSteps > newRow$SolutionSteps) {
    newRow$SolutionSteps <- "Direct Tie"
  } else if(newRow$SolutionSteps == DRow$SolutionSteps & DRow$SolutionSteps > ThenRow$SolutionSteps) {
    newRow$SolutionSteps <- "Not Hybrid Tie"
  } else if(newRow$SolutionSteps == ThenRow$SolutionSteps & DRow$SolutionSteps == ThenRow$SolutionSteps & ThenRow$SolutionSteps > 0.0) {
    newRow$SolutionSteps <- "All Tie"
  } else {
    newRow$SolutionSteps <- "None"
  }

  overlapData <- rbind(overlapData, newRow)
}  

# Re-order the factors
overlapData$SolutionSteps <- factor(overlapData$SolutionSteps, levels=c("CPPN2GAN","CPPNThenDirect2GAN","Direct2GAN","CPPN Tie","Direct Tie","Not Hybrid Tie","All Tie","None"))

outputFile <- paste("MarioLevelsDecorateNSLeniency-BEST.",nameEnd,".heat.pdf",sep="")
pdf(outputFile,height=3.5)  
result <- ggplot(overlapData, aes(x=decorationBin, y=nsBin, fill=factor(SolutionSteps))) +
  geom_tile() +
  facet_wrap(~leniencyBin, ncol=5, labeller = labeller(leniencyBin = leniencyLabals)) +
  #scale_fill_gradient(low="white", high="orange") +
  #scale_fill_viridis(discrete=FALSE, limits = c(0,500), oob = squish) +
  scale_fill_manual(values=c("#B4E956", "#000EEE", "#56B4E9", "#999999")) +
  xlab("Decoration Frequency Bin") +
  ylab("Space Coverage Bin") +
  labs(fill = "Highest Quality: ") +
  # Puts room count in the plot for each bin
  #geom_text(aes(label = ifelse(wallBin == 5 & waterBin == 4, roomBin, NA)), 
  #          nudge_x = 2.5,nudge_y = 3) +
  #annotation_custom(grob) +
  theme(strip.background = element_blank(),
        #strip.text = element_blank(),
        legend.position="top",
        legend.direction = "horizontal",
        legend.key.width = unit(10,"points"),
        legend.key.height = unit(10,"points"),
        legend.text = element_text(size=10),
        panel.spacing.x=unit(0.001, "points"),
        panel.spacing.y=unit(0.001, "points"),
        axis.ticks = element_blank(),
        axis.text = element_blank())
print(result)
dev.off()

print(paste("Saved:",outputFile))
print("Finished")


