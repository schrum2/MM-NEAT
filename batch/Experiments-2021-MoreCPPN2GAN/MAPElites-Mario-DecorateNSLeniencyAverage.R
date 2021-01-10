#!/usr/bin/env Rscript

# Possible to set "generation"
args = commandArgs(trailingOnly=TRUE)

setwd("../../mariolevelsdecoratensleniency")
#setwd("G:\\My Drive\\Research\\2021-IEEE-ToG-CPPNThenDirectToGAN\\MM-NEAT\\mariolevelsdecoratensleniency")
print("Load data")
types <- list("CPPN2GAN","CPPNThenDirect2GAN","Direct2GAN","Combined")

for(typePrefix in types) {
#typePrefix <- "CPPN2GAN"

for(i in 0:29) {
  dataFile <- paste(typePrefix,i,"/MarioLevelsDecorateNSLeniency-",typePrefix,i,"_MAPElites_log.txt",sep="")
  map <- read.table(dataFile)
  print(dataFile)
  if (length(args)==0) {
    # Only the final archive matters
    lastRow <- map[map$V1 == nrow(map) - 1, ]
    nameEnd <- "LAST"
  } else {
    lastRow <- map[map$V1 == strtoi(args[1], base = 0L) - 1, ]
    nameEnd <- paste("Gen",args[1],sep="")
  }
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

allData <- data.frame(archive, decorationBin, nsBin, leniencyBin)

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

outputFile <- paste("MarioLevelsDecorateNSLeniency-",typePrefix,"-AVG.",nameEnd,".heat.pdf",sep="")
pdf(outputFile,height=3.5)  
result <- ggplot(allData, aes(x=decorationBin, y=nsBin, fill=SolutionSteps)) +
  geom_tile() +
  facet_wrap(~leniencyBin, ncol=5, labeller = labeller(leniencyBin = leniencyLabals)) +
  #scale_fill_gradient(low="white", high="orange") +
  scale_fill_viridis(discrete=FALSE, limits = c(0,500), oob = squish) +
  xlab("Decoration Frequency Bin") +
  ylab("Space Coverage Bin") +
  labs(fill = "Solution Path Length") +
  # Puts room count in the plot for each bin
  #geom_text(aes(label = ifelse(wallBin == 5 & waterBin == 4, roomBin, NA)), 
  #          nudge_x = 2.5,nudge_y = 3) +
  #annotation_custom(grob) +
  theme(strip.background = element_blank(),
        #strip.text = element_blank(),
        legend.position="top",
        legend.direction = "horizontal",
        legend.key.width = unit(70,"points"),
        panel.spacing.x=unit(0.001, "points"),
        panel.spacing.y=unit(0.001, "points"),
        axis.ticks = element_blank(),
        axis.text = element_blank())
print(result)
dev.off()

print(paste("Saved:",outputFile))
print("Finished")

}
