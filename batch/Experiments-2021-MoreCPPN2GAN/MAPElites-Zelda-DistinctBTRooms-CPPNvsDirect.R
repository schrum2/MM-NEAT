#!/usr/bin/env Rscript
args = commandArgs(trailingOnly=TRUE)
if (length(args)==0) {
  stop("Supply file name of MAP Elites log for Zelda", call.=FALSE)
}

#setwd("E:\\Users\\he_de\\workspace\\MM-NEAT")
print("Load data")
#map <- read.table("E:\\Users\\he_de\\workspace\\MM-NEAT\\zeldacppnthendirect\\NewMAPElites1\\ZeldaCPPNThenDirect-NewMAPElites1_MAPElites_log.txt")
map <- read.table(args[1], na.strings = c("X"))
# Only the final archive matters
lastRow <- map[map$V1 == nrow(map) - 1, ]
lastRow[ is.na(lastRow) ] <- -Inf
archive <- data.frame(matrix(unlist(lastRow[2:length(lastRow)]), nrow=(length(lastRow)-1), byrow=T))
names(archive) <- "Type"

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
#library(viridis)
library(stringr)

dropRooms0 <- filter(allData, roomBin > 0)

for (b in seq(1,maxNumRooms)) {
  dropRooms0 <- filter(dropRooms0, backTrackBin < roomBin)
}

dropRooms0$Type[dropRooms0$Type == -1] <- "Empty"
dropRooms0$Type[dropRooms0$Type == 1] <- "CPPN"
dropRooms0$Type[dropRooms0$Type == 2] <- "Direct"

print("Create plot and save to file")

outputFile <- str_replace(args[1],"txt","type.pdf")
pdf(outputFile)  
result <- ggplot(dropRooms0, aes(x=backTrackBin, y=distinctBin, fill=factor(Type))) +
  geom_tile() +
  facet_wrap(~roomBin) +
  scale_fill_manual(values=c("#E69F00", "#56B4E9", "#999999")) +
  #scale_fill_gradient(low="white", high="orange") +
  #scale_fill_viridis(discrete=FALSE) +
  xlab("Backtracked Rooms") +
  ylab("Distinct Rooms") +
  labs(fill = "") +
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
