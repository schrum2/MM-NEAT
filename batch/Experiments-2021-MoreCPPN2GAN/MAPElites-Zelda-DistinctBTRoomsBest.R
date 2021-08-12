# Possible to set "generation"
args = commandArgs(trailingOnly=TRUE)

setwd("../../zeldadungeonsdistinctbtrooms")
print("Load data")
types <- list("CPPN2GAN","CPPNThenDirect2GAN","Direct2GAN")

allData = array()

type <- 0
for(typePrefix in types) {
  #typePrefix <- "CPPN2GAN"
  
  for(i in 0:29) {
    dataFile <- paste(typePrefix,i,"/ZeldaDungeonsDistinctBTRooms-",typePrefix,i,"_MAPElites_log.txt",sep="")
    map <- read.table(dataFile, na.strings = c("X"))
    print(dataFile)
    #map <- read.table(args[1])
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
    names(archive) <- "PercentTraversed"


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

typeBin <- rep(type, (maxNumRooms+1)*(maxNumRooms+1)*(maxNumRooms+1))
typeBin <- data.frame(typeBin)

temp <- data.frame(archive, typeBin, distinctBin, backTrackBin, roomBin)
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

dropRooms0 <- filter(allData, roomBin > 0)

for (b in seq(1,maxNumRooms)) {
  dropRooms0 <- filter(dropRooms0, backTrackBin < roomBin)
}

allData <- dropRooms0

print("Create plot and save to file")

# Fill with data on overlaping bins
overlapData = NULL
numBins <- 8450
for(i in 1:numBins) { 
  newRow <- allData[i, ] # CPPN2GAN
  ThenRow <- allData[i+numBins, ] #CPPNThenDirect2GAN
  DRow <- allData[i+2*numBins, ] #Direct2GAN
  
  if(newRow$PercentTraversed > ThenRow$PercentTraversed & newRow$PercentTraversed > DRow$PercentTraversed) {
    newRow$PercentTraversed <- "CPPN2GAN"
  } else if(ThenRow$PercentTraversed > newRow$PercentTraversed & ThenRow$PercentTraversed > DRow$PercentTraversed) {
    newRow$PercentTraversed <- "CPPNThenDirect2GAN"
  } else if(DRow$PercentTraversed > newRow$PercentTraversed & DRow$PercentTraversed > ThenRow$PercentTraversed) {
    newRow$PercentTraversed <- "Direct2GAN"
  } else if(newRow$PercentTraversed == ThenRow$PercentTraversed & newRow$PercentTraversed > DRow$PercentTraversed) {
    newRow$PercentTraversed <- "CPPN Tie"
  } else if(DRow$PercentTraversed == ThenRow$PercentTraversed & DRow$PercentTraversed > newRow$PercentTraversed) {
    newRow$PercentTraversed <- "Direct Tie"
  } else if(newRow$PercentTraversed == DRow$PercentTraversed & DRow$PercentTraversed > ThenRow$PercentTraversed) {
    newRow$PercentTraversed <- "Not Hybrid Tie"
  } else if(newRow$PercentTraversed == ThenRow$PercentTraversed & DRow$PercentTraversed == ThenRow$PercentTraversed & ThenRow$PercentTraversed > 0.0) {
    newRow$PercentTraversed <- "All Tie"
  } else {
    newRow$PercentTraversed <- "None"
  }
  
  overlapData <- rbind(overlapData, newRow)
}  

# Re-order the factors
overlapData$PercentTraversed <- factor(overlapData$PercentTraversed, levels=c("CPPN2GAN","CPPNThenDirect2GAN","Direct2GAN","CPPN Tie","Direct Tie","Not Hybrid Tie","All Tie","None"))
namedcolors        <- c("#B4E956" ,"#000EEE"           ,"#F040AA"   ,"#56B4E9" ,"#A52A2A"   ,"#EEE000"       ,"#E69F00","#999999")
names(namedcolors) <- c("CPPN2GAN","CPPNThenDirect2GAN","Direct2GAN","CPPN Tie","Direct Tie","Not Hybrid Tie","All Tie","None")

outputFile <- paste("ZeldaDungeonsDistinctBTRooms-BEST.",nameEnd,".heat.pdf",sep="")
#outputFile <- str_replace(args[1],"txt","heat.pdf")
pdf(outputFile)  
result <- ggplot(overlapData, aes(x=backTrackBin, y=distinctBin, fill=factor(PercentTraversed))) +
  geom_tile() +
  facet_wrap(~roomBin) +
  #scale_fill_gradient(low="white", high="orange") +
  #scale_fill_viridis(discrete=FALSE) +
  scale_fill_manual(values=namedcolors) +
  xlab("Backtracked Rooms") +
  ylab("Distinct Rooms") +
  ggtitle("Highest Quality:") +
  labs(fill = "") + # Made lengend too big
  # Puts room count in the plot for each bin
  geom_text(aes(label = ifelse(distinctBin == 20 & backTrackBin == 0, roomBin, NA)), 
            nudge_x = 28,nudge_y = 3) +
  #annotation_custom(grob) +
  theme(strip.background = element_blank(),
        strip.text = element_blank(),
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


