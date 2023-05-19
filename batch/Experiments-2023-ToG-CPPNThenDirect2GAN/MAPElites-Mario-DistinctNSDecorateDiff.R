# Possible to set "generation"
args = commandArgs(trailingOnly=TRUE)

setwd("../../mariolevelsdistinctnsdecorate")
#setwd("E:\\Users\\he_de\\workspace\\GameGAN")

print("Load data")
types <- list("CPPN2GAN","CPPNThenDirect2GAN","Direct2GAN")

allData = array()

type <- 0
for(typePrefix in types) {
  #typePrefix <- "CPPN2GAN"
  
  for(i in 0:29) {
    dataFile <- paste(typePrefix,i,"/MarioLevelsDistinctNSDecorate-",typePrefix,i,"_MAPElites_log.txt",sep="")
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

typeBin <- rep(type, 1100)
typeBin <- data.frame(typeBin)

temp <- data.frame(archive, typeBin, distinctBin, nsBin, decorateBin)
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

drop0 <- filter(allData, distinctBin > 0)
allData <- drop0

distinctLabels <- function(num) {
  paste("Distinct Segments:",num)
}

# Fill with data on overlaping bins
overlapData = NULL
numBins <- 1000
for(i in 1:numBins) { # 1000 bins
  newRow <- allData[i, ] # CPPN2GAN
  ThenRow <- allData[i+numBins, ] #CPPNThenDirect2GAN
  DRow <- allData[i+2*numBins, ] #Direct2GAN
  
  if(newRow$SolutionSteps > 0.0 & ThenRow$SolutionSteps > 0.0 & DRow$SolutionSteps > 0.0) {
    newRow$SolutionSteps <- "All"
  } else if(newRow$SolutionSteps > 0.0 & ThenRow$SolutionSteps > 0.0 & DRow$SolutionSteps == 0.0) {
    newRow$SolutionSteps <- "No Direct2GAN"
  } else if(newRow$SolutionSteps > 0.0 & ThenRow$SolutionSteps == 0.0 & DRow$SolutionSteps > 0.0) {
    newRow$SolutionSteps <- "No CPPNThenDirect2GAN"
  } else if(newRow$SolutionSteps == 0.0 & ThenRow$SolutionSteps > 0.0 & DRow$SolutionSteps > 0.0) {
    newRow$SolutionSteps <- "No CPPN2GAN"
  } else if(newRow$SolutionSteps > 0.0 & ThenRow$SolutionSteps == 0.0 & DRow$SolutionSteps == 0.0) {
    newRow$SolutionSteps <- "Only CPPN2GAN"
  } else if(newRow$SolutionSteps == 0.0 & ThenRow$SolutionSteps > 0.0 & DRow$SolutionSteps == 0.0) {
    newRow$SolutionSteps <- "Only CPPNThenDirect2GAN"
  } else if(newRow$SolutionSteps == 0.0 & ThenRow$SolutionSteps == 0.0 & DRow$SolutionSteps > 0.0) {
    newRow$SolutionSteps <- "Only Direct2GAN"
  } else {
    newRow$SolutionSteps <- "None"
  }
  
  overlapData <- rbind(overlapData, newRow)
}  

# Re-order the factors
overlapData$SolutionSteps <- factor(overlapData$SolutionSteps, levels=c("All","No CPPNThenDirect2GAN","No CPPN2GAN","No Direct2GAN","Only CPPNThenDirect2GAN","Only CPPN2GAN","Only Direct2GAN","None"))
namedcolors        <- c("#E69F00","#EEE000"              ,"#A52A2A"    ,"#56B4E9"      ,"#000EEE"                ,"#B4E956"      ,"#F040AA"        ,"#999999")
names(namedcolors) <- c("All"    ,"No CPPNThenDirect2GAN","No CPPN2GAN","No Direct2GAN","Only CPPNThenDirect2GAN","Only CPPN2GAN","Only Direct2GAN","None")

outputFile <- paste("MarioLevelsDistinctNSDecorate-DIFF.",nameEnd,".heat.pdf",sep="")
pdf(outputFile,height=3.5)  
result <- ggplot(overlapData, aes(x=decorateBin, y=nsBin, fill=factor(SolutionSteps))) +
  geom_tile() +
  facet_wrap(~distinctBin, ncol=5, labeller = labeller(distinctBin = distinctLabels)) +
  #scale_fill_gradient(low="white", high="orange") +
  #scale_fill_viridis(discrete=FALSE, limits = c(0,500), oob = squish) +
  scale_fill_manual(values=namedcolors) +
  xlab("Alternating Decoration Bin") +
  ylab("Alternating Space Coverage Bin") +
  labs(fill = "Occupied by: ") +
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


