#!/usr/bin/env Rscript
args = commandArgs(trailingOnly=TRUE)
if (length(args)==0) {
  stop("Supply file name of MAP Elites log for Mario", call.=FALSE)
}

#setwd("E:\\Users\\he_de\\workspace\\GameGAN")
print("Load data")
#map <- read.table("mariogan/MAPElites0/MarioGAN-MAPElites0_MAPElites_log.txt")
map <- read.table(args[1])
# Only the final archive matters
lastRow <- map[map$V1 == nrow(map) - 1, ]
archive <- data.frame(matrix(unlist(lastRow[2:length(lastRow)]), nrow=(length(lastRow)-1), byrow=T))
names(archive) <- "SolutionSteps"

# Add data indicating how the data is binned, based on convention of how
# output data is organized

print("Organize bins")

groundBin <- append(rep(0, 10*10), rep(1, 10*10))
groundBin <- append(groundBin, rep(2, 10*10))
groundBin <- append(groundBin, rep(3, 10*10))
groundBin <- append(groundBin, rep(4, 10*10))
groundBin <- append(groundBin, rep(5, 10*10))
groundBin <- append(groundBin, rep(6, 10*10))
groundBin <- append(groundBin, rep(7, 10*10))
groundBin <- append(groundBin, rep(8, 10*10))
groundBin <- append(groundBin, rep(9, 10*10))

groundBin <- data.frame(groundBin)

treasureBin <- append(rep(0, 10), rep(1, 10))
treasureBin <- append(treasureBin, rep(2, 10))
treasureBin <- append(treasureBin, rep(3, 10))
treasureBin <- append(treasureBin, rep(4, 10))
treasureBin <- append(treasureBin, rep(5, 10))
treasureBin <- append(treasureBin, rep(6, 10))
treasureBin <- append(treasureBin, rep(7, 10))
treasureBin <- append(treasureBin, rep(8, 10))
treasureBin <- append(treasureBin, rep(9, 10))

treasureBin <- rep(treasureBin, 10)
treasureBin <- data.frame(treasureBin)

enemyBin <- rep(seq(-5,4),10*10)
enemyBin <- data.frame(enemyBin)

allData <- data.frame(archive, groundBin, treasureBin, enemyBin)

###############################################

library(ggplot2)
library(dplyr)
library(viridis)
library(stringr)
library(scales)

print("Create plot and save to file")

groundLabels <- function(num) {
  paste("Ground Dimension:",num)
}

outputFile <- str_replace(args[1],"txt","heat.pdf")
pdf(outputFile,height=3.5)  
result <- ggplot(allData, aes(x=enemyBin, y=treasureBin, fill=SolutionSteps)) +
  geom_tile() +
  facet_wrap(~groundBin, ncol=5, labeller = labeller(groundBin = groundLabels)) +
  #scale_fill_gradient(low="white", high="orange") +
  scale_fill_viridis(discrete=FALSE, limits = c(1,600), oob = squish) +
  xlab("Enemies") +
  ylab("Treasures") +
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
