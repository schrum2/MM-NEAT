#!/usr/bin/env Rscript
args = commandArgs(trailingOnly=TRUE)
if (length(args)==0) {
  stop("Supply file name of MAP Elites log for Zelda", call.=FALSE)
}

#setwd("E:\\Users\\he_de\\workspace\\MM-NEAT")
print("Load data")
#map <- read.table("E:\\Users\\he_de\\workspace\\MM-NEAT\\zeldacppnthendirect\\NewMAPElites1\\ZeldaCPPNThenDirect-NewMAPElites1_MAPElites_log.txt")
map <- read.table(args[1])
# Only the final archive matters
lastRow <- map[map$V1 == nrow(map) - 1, ]
archive <- data.frame(matrix(unlist(lastRow[2:length(lastRow)]), nrow=(length(lastRow)-1), byrow=T))
names(archive) <- "PercentTraversed"

# Add data indicating how the data is binned, based on convention of how
# output data is organized

maxNumRooms <- 25

print("Organize bins")

distinctBin <- rep(0, (maxNumRooms+1)*(maxNumRooms+1))
for (b in seq(1,maxNumRooms)) {
  distinctBin <- append(distinctBin, rep(b, (maxNumRooms+1)*(maxNumRooms+1)))
}
distinctBin <- data.frame(distinctBin)


backTrackBin <- rep(0, maxNumRooms+1)
for (b in seq(1,maxNumRooms)) {
  backTrackBin <- append(backTrackBin, rep(b, maxNumRooms+1))
}

backTrackBin <- rep(backTrackBin, maxNumRooms+1)
backTrackBin <- data.frame(backTrackBin)

roomBin <- rep(seq(0,maxNumRooms),(maxNumRooms+1)*(maxNumRooms+1))
roomBin <- data.frame(roomBin)

allData <- data.frame(archive, distinctBin, backTrackBin, roomBin)

###############################################

library(ggplot2)
library(dplyr)
library(viridis)
library(stringr)

dropRooms0 <- filter(allData, roomBin > 0)

for (b in seq(1,maxNumRooms)) {
  dropRooms0 <- filter(dropRooms0, backTrackBin < roomBin)
}

print("Create plot and save to file")

outputFile <- str_replace(args[1],"txt","heat.pdf")
pdf(outputFile)  
result <- ggplot(dropRooms0, aes(x=backTrackBin, y=distinctBin, fill=PercentTraversed)) +
  geom_tile() +
  facet_wrap(~roomBin) +
  #scale_fill_gradient(low="white", high="orange") +
  scale_fill_viridis(discrete=FALSE) +
  xlab("Backtracked Rooms") +
  ylab("Distinct Rooms") +
  labs(fill = "Percent Rooms Traversed") +
  # Puts room count in the plot for each bin
  geom_text(aes(label = ifelse(distinctBin == 20 & backTrackBin == 0, roomBin, NA)), 
            nudge_x = 28,nudge_y = 3) +
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
