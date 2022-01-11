#!/usr/bin/env Rscript
args = commandArgs(trailingOnly=TRUE)
if (length(args)==0) {
  stop("Supply experiment name: MarioLevelsDecorateNSLeniency, MarioLevelsDistinctNSDecorate, ZeldaDungeonsDistinctBTRooms, ZeldaDungeonsWallWaterRooms", call.=FALSE)
}

library(reshape)
library(ggplot2)
library(tidyr)
library(plyr)
library(dplyr)

legendX = args[2]
legendY = args[3]

# Get log prefix
logPrefix <- args[1] # "MarioLevelsDecorateNSLeniency"
# Set working directory and move into it
resultDir <- tolower(logPrefix)
setwd(paste("../../",resultDir,sep=""))
# Which score/objective?
cppnIndex <- 2  # CPPN count
directIndex <- 3  # Direct count
# Determine the different experimental conditions
t <- "CPPNThenDirect2GAN"
# Initialize empty data
evolutionData <- data.frame(generation = integer(), score = double())

# Get each directory starting with the type name, followed by digits
directories <- list.files(".",pattern=paste("^",t,"\\d*", sep = ""))
for(d in directories) {
    # Read each individual file
    fileName <- paste(d,"/",logPrefix,"-",d,"_cppnToDirect_log.txt", sep = "")
    temp <- read.table(file = fileName, sep = '\t', header = FALSE)
    # Rename relevant column
    colnames(temp)[1] <- "Generation"
    colnames(temp)[cppnIndex] <- "CPPN"
    colnames(temp)[directIndex] <- "Direct"
    stacked <- melt(temp, id.vars=c("Generation"), var='type') # Transforms (Gen, CPPN, Direct) cols into (Gen, type, value) where type is CPPN or Direct 
    # Add data
    evolutionData <- rbind(evolutionData, data.frame(generation = stacked$Generation, 
                                       type = stacked$type,
                                       run = substring(d,nchar(t)+1), # Get the number following the type
                                       score = c(stacked$value) ))
}

maxScore <- max(evolutionData$score)
# For some weird reason, some runs end at 1000, and others end at 999. Make uniform.
evolutionData <- evolutionData[!(evolutionData$generation == 1000),]
maxGeneration = 999

# Extract states: mean, lower confidence bound, upper confidence bound
evolutionStats <- evolutionData %>%
  group_by(type, generation) %>%
  summarize(n = length(run), avgScore = mean(score), stdevScore = sd(score)) %>%
  mutate(stderrScore = qt(0.975, df = n - 1)*stdevScore/sqrt(n)) %>%
  mutate(lowScore = avgScore - stderrScore, highScore = avgScore + stderrScore)

types <- list("CPPN", "Direct")

# Difference between generation and generated individuals
evolutionStats$generation <- evolutionStats$generation * 100

saveFile <- paste("AVGDirectVsCPPN-",logPrefix,".pdf",sep="")
#png(saveFile, width=2000, height=1000)
pdf(saveFile, width=4, height=2.5)
v <- ggplot(evolutionStats, aes(x = generation, y = avgScore, color = type)) +
  geom_ribbon(aes(ymin = lowScore, ymax = highScore, fill = type), alpha = 0.05, show.legend = FALSE) +
  geom_line(size = 0.3) + 
  # Should the 10 here be a parameter? Controls frequency of point plotting. Change size too?
  geom_point(data = subset(evolutionStats, generation %% 5000 == 0), size = 2, aes(shape = type)) + 
  # This can be adapted to indicate significant pairwise differences.
  # However, some work needs to be done to make sure testData compares the relevant cases
  #geom_point(data = testData, 
  #           aes(x = generation, 
  #               y = if_else(significant, -spacePerComparison*match(type, comparisonList), -100000), 
  #               size = 5, color = type, shape = type), 
  #           alpha = 0.5, show.legend = FALSE) +
  # For separate plots
  #facet_wrap(~type) + 
  #ggtitle("INSERT COOL TITLE HERE") +
  #coord_cartesian(ylim=c(-spaceForTests,maxScore)) +
  #scale_color_discrete(breaks=types) +
  guides(shape = guide_legend(reverse=FALSE), color = guide_legend(reverse=FALSE)) +
  #guides(size = FALSE, alpha = FALSE) +
  ylab("Number of Filled Bins") +
  xlab("Generated Individuals") +
  theme(
    plot.title = element_text(size=7, face="bold"),
    axis.title.x = element_text(size=7, face="bold"),
    axis.text.x = element_text(size=7, face="bold"),
    axis.title.y = element_text(size=7, face="bold"),
    axis.text.y = element_text(size=7, face="bold"),
    legend.title = element_blank(),
    legend.text = element_text(size=7, face="bold"),
    legend.position = c(legendX, legendY)
  )
print(v)
dev.off()

print("Success!")
print(paste("File saved in ",getwd(),"/",saveFile,sep=""))