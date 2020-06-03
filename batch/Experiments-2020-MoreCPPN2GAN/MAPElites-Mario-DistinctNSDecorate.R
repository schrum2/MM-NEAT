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

distinctBin <- append(rep(0, 10*10), rep(1, 10*10))
distinctBin <- append(distinctBin, rep(2, 10*10))
distinctBin <- append(distinctBin, rep(3, 10*10))
distinctBin <- append(distinctBin, rep(4, 10*10))
distinctBin <- append(distinctBin, rep(5, 10*10))
distinctBin <- append(distinctBin, rep(6, 10*10))
distinctBin <- append(distinctBin, rep(7, 10*10))
distinctBin <- append(distinctBin, rep(8, 10*10))
distinctBin <- append(distinctBin, rep(9, 10*10))
distinctBin <- append(distinctBin, rep(10,10*10))

distinctBin <- data.frame(distinctBin)

nsBin <- append(rep(0, 10), rep(1, 10))
nsBin <- append(nsBin, rep(2, 10))
nsBin <- append(nsBin, rep(3, 10))
nsBin <- append(nsBin, rep(4, 10))
nsBin <- append(nsBin, rep(5, 10))
nsBin <- append(nsBin, rep(6, 10))
nsBin <- append(nsBin, rep(7, 10))
nsBin <- append(nsBin, rep(8, 10))
nsBin <- append(nsBin, rep(9, 10))

nsBin <- rep(nsBin, 11)
nsBin <- data.frame(nsBin)

decorateBin <- rep(seq(0,9),11*10)
decorateBin <- data.frame(decorateBin)

allData <- data.frame(archive, distinctBin, nsBin, decorateBin)

###############################################

library(ggplot2)
library(dplyr)
library(viridis)
library(stringr)
library(scales)

print("Create plot and save to file")

drop0 <- filter(allData, distinctBin > 0)

distinctLabels <- function(num) {
  paste("Distinct Segments:",num)
}

outputFile <- str_replace(args[1],"txt","heat.pdf")
pdf(outputFile,height=3.5)  
result <- ggplot(drop0, aes(x=decorateBin, y=nsBin, fill=SolutionSteps)) +
  geom_tile() +
  facet_wrap(~distinctBin, ncol=5, labeller = labeller(distinctBin = distinctLabels)) +
  #scale_fill_gradient(low="white", high="orange") +
  scale_fill_viridis(discrete=FALSE, limits = c(250,500), oob = squish) +
  xlab("Alternating Decoration Bin") +
  ylab("Alternating Space Coverage Bin") +
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
