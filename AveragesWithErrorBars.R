#!/usr/bin/env Rscript
library(ggplot2)
library(tidyr)
library(plyr)
library(dplyr)

args = commandArgs(trailingOnly=TRUE)

if (length(args) < 3) {
  print("Specify name of result directory, a log prefix, and a score index as command line parameters.")
  print("Example: Rscript.exe AveragesWithErrorBars.R tetris Tetris 0")
  stop()
} 
# Set working directory and move into it
resultDir <- args[1]
setwd(paste("./",resultDir,sep=""))
# Get log prefix
logPrefix <- args[2]
# Which score/objective?
# Add 1 to skip generations, each score takes up four columns, but the third is the max
scoreIndex <- 1 + (strtoi(args[3], base = 0L) * 4) + 3
# Determine the different experimental conditions
types <- unique(sub("\\d+$","",list.files(".",pattern="[a-zA-Z]+\\d+$")))
# Remove any that were excluded at the command line
index = 4
while(index <= length(args)) {
  print(paste("Excluding ",args[index]," from data.",sep = ""))
  types <- types[types != args[index]]
  index <- index + 1
}
# Initialize empty data
evolutionData <- data.frame(generation = integer(), score = double())
# Exach experimental condition
for(t in types) {
  # Get each directory starting with the type name, followed by digits
  directories <- list.files(".",pattern=paste("^",t,"\\d*", sep = ""))
  for(d in directories) {
    # Read each individual file
    temp <- read.table(file = paste(d,"/",logPrefix,"-",d,"_parents_log.txt", sep = ""), sep = '\t', header = FALSE)
    # Rename relevant column
    colnames(temp)[scoreIndex] <- "score"
    # Add data
    evolutionData <- rbind(evolutionData, data.frame(generation = temp$V1, 
                                       type = paste(t,sep=""),
                                       run = substring(d,nchar(t)+1), # Get the number following the type
                                       score = c(temp[scoreIndex])))
  }
}

maxScore = max(evolutionData$score)
maxGeneration = max(evolutionData$generation)

# Do comparative t-tests
testData <- data.frame(generation = integer(), p = double(), significant = logical())
comparisonList <- list()

# This testData is actually ignored below (commented out). You can uncomment that to
# get all pair-wise differences. However, it is probably better to tweak the selection of
# specific conditions that are compared on a pair-wise basis.

for(i in seq(1,length(types)-1,1)) {
  for(j in seq(i+1,length(types),1)) {
    t1 = types[i]
    t2 = types[j]
    typeName <- paste(t1,"Vs",t2, sep="")
    comparisonList <- append(comparisonList, typeName)
    for(g in seq(1,maxGeneration,1)) {
      t1Data <- evolutionData %>% filter(generation == g, type == t1) %>% select(score)
      t2Data <- evolutionData %>% filter(generation == g, type == t2) %>% select(score)
      if(length(t1Data$score) > 1 && length(t2Data$score)) {
        tresult <- t.test(t1Data, t2Data)
        testData <- rbind(testData, data.frame(type = typeName,
                                               generation = g,
                                               p = tresult[['p.value']],
                                               significant = tresult[['p.value']] < 0.05))
      }
    }
  }
}

# Extract states: mean, lower confidence bound, upper confidence bound
evolutionStats <- evolutionData %>%
  group_by(type, generation) %>%
  summarize(n = length(run), avgScore = mean(score), stdevScore = sd(score)) %>%
  mutate(stderrScore = qt(0.975, df = n - 1)*stdevScore/sqrt(n)) %>%
  mutate(lowScore = avgScore - stderrScore, highScore = avgScore + stderrScore)

# Configure space at bottom for t-test data
spaceForTests <- maxScore / 6
spacePerComparison <- spaceForTests / length(comparisonList)
  
saveFile <- paste("AVG-",resultDir,args[3],".png",sep="")
png(saveFile, width=2000, height=1000)
v <- ggplot(evolutionStats, aes(x = generation, y = avgScore, color = type)) +
  geom_ribbon(aes(ymin = lowScore, ymax = highScore, fill = type), alpha = 0.05, show.legend = FALSE) +
  geom_line(size = 1.5) + 
  # Should the 10 here be a parameter? Controls frequency of point plotting. Change size too?
  geom_point(data = subset(evolutionStats, generation %% 10 == 0), size = 15, aes(shape = type)) + 
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
  coord_cartesian(ylim=c(-spaceForTests,maxScore)) +
  scale_color_discrete(breaks=types) +
  guides(size = FALSE, alpha = FALSE) +
  ylab("Average Score") +
  xlab("Generation") +
  theme(
    plot.title = element_text(size=25, face="bold"),
    axis.title.x = element_text(size=25, face="bold"),
    axis.text.x = element_text(size=25, face="bold"),
    axis.title.y = element_text(size=25, face="bold"),
    axis.text.y = element_text(size=25, face="bold"),
    legend.title = element_blank(),
    legend.text = element_text(size=25, face="bold"),
    legend.position = c(0.8, 0.2)
  )
print(v)
dev.off()

print("Success!")
print(paste("File saved in ",getwd(),"/",saveFile,sep=""))