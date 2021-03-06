#!/usr/bin/env Rscript
args = commandArgs(trailingOnly=TRUE)
if (length(args)==0) {
  stop("Supply file name of MAP Elites log for Zelda", call.=FALSE)
}

#setwd("E:\\Users\\he_de\\workspace\\GameGAN")
print("Load data")
#map <- read.table("zeldacppntogan/MAPElites3/ZeldaCPPNtoGAN-MAPElites3_MAPElites_log.txt")
map <- read.table(args[1])
# Only the final archive matters
lastRow <- map[map$V1 == nrow(map) - 1, ]
archive <- data.frame(matrix(unlist(lastRow[2:length(lastRow)]), nrow=(length(lastRow)-1), byrow=T))
names(archive) <- "Type"

# Add data indicating how the data is binned, based on convention of how
# output data is organized

print("Organize bins")

maxNumRooms <- 25

wallBin <- append(rep(0, 10*(maxNumRooms+1)), rep(1, 10*(maxNumRooms+1)))
wallBin <- append(wallBin, rep(2, 10*(maxNumRooms+1)))
wallBin <- append(wallBin, rep(3, 10*(maxNumRooms+1)))
wallBin <- append(wallBin, rep(4, 10*(maxNumRooms+1)))
wallBin <- append(wallBin, rep(5, 10*(maxNumRooms+1)))
wallBin <- append(wallBin, rep(6, 10*(maxNumRooms+1)))
wallBin <- append(wallBin, rep(7, 10*(maxNumRooms+1)))
wallBin <- append(wallBin, rep(8, 10*(maxNumRooms+1)))
wallBin <- append(wallBin, rep(9, 10*(maxNumRooms+1)))

wallBin <- data.frame(wallBin)

waterBin <- append(rep(0, (maxNumRooms+1)), rep(1, (maxNumRooms+1)))
waterBin <- append(waterBin, rep(2, (maxNumRooms+1)))
waterBin <- append(waterBin, rep(3, (maxNumRooms+1)))
waterBin <- append(waterBin, rep(4, (maxNumRooms+1)))
waterBin <- append(waterBin, rep(5, (maxNumRooms+1)))
waterBin <- append(waterBin, rep(6, (maxNumRooms+1)))
waterBin <- append(waterBin, rep(7, (maxNumRooms+1)))
waterBin <- append(waterBin, rep(8, (maxNumRooms+1)))
waterBin <- append(waterBin, rep(9, (maxNumRooms+1)))

waterBin <- rep(waterBin, 10)
waterBin <- data.frame(waterBin)

roomBin <- rep(seq(0,maxNumRooms),10*10)
roomBin <- data.frame(roomBin)

allData <- data.frame(archive, wallBin, waterBin, roomBin)

###############################################

library(ggplot2)
library(dplyr)
library(viridis)
library(stringr)

dropUpperRightCorners <- filter(allData, wallBin + waterBin <= 9)
# Bin for dungeons with 0 rooms doesn't actually have anything
dropRooms0 <- filter(dropUpperRightCorners, roomBin > 0)

dropRooms0$Type[dropRooms0$Type == -1] <- "Empty"
dropRooms0$Type[dropRooms0$Type == 1] <- "CPPN"
dropRooms0$Type[dropRooms0$Type == 2] <- "Direct"

print("Create plot and save to file")

outputFile <- str_replace(args[1],"txt","heat.pdf")
pdf(outputFile)  
result <- ggplot(dropRooms0, aes(x=waterBin, y=wallBin, fill=factor(Type))) +
  geom_tile() +
  facet_wrap(~roomBin) +
  scale_fill_manual(values=c("#E69F00", "#56B4E9", "#999999")) +
  #scale_fill_gradient(low="white", high="orange") +
  #scale_fill_viridis(discrete=FALSE) +
  xlab("Water Percentage Bin") +
  ylab("Wall Percentage Bin") +
  labs(fill = "") +
  # Puts room count in the plot for each bin
  geom_text(aes(label = ifelse(wallBin == 5 & waterBin == 4, roomBin, NA)), 
            nudge_x = 2.5,nudge_y = 3) +
  #annotation_custom(grob) +
  theme(strip.background = element_blank(),
        strip.text = element_blank(),
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
