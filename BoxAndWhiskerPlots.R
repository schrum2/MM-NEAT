#!/usr/bin/env Rscript
library(ggplot2)
library(tidyr)
library(plyr)
library(dplyr)

args = commandArgs(trailingOnly=TRUE)

if (length(args) < 3) {
  print("Specify name of result directory, a log prefix, and a score index as command line parameters.")
  print("Example: Rscript.exe BoxAndWhiskerPlots.R tetris Tetris 0")
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

# This collects the boxplot data, but I don't know how to plot this data as boxplots
#evolutionStats <- evolutionData %>%
#  group_by(type, generation) %>%
#  summarize(n = length(run), 
#            medianScore = median(score), 
#            minScore = min(score),
#            maxScore = max(score),
#            firstQuartile = quantile(score, 0.25),
#            thirdQuartile = quantile(score, 0.75))
  
alteredData <- evolutionData %>% 
  mutate(plotInterval = generation %% 10 == 0) %>%  # Make this a parameter?
  mutate(typeRunCombo = paste(type,run)) %>%
  mutate(typeGenCombo = paste(type,generation)) %>%
  group_by(typeGenCombo) %>%
  mutate(medianScoreByGeneration = median(score)) %>%
  ungroup() %>%
  mutate(genF = as.factor(generation)) %>% # Boxplot needs generation as factor
  select(-generation) # Remove numeric generation
  
f <- function(theData) {
  result <- theData %>% filter(plotInterval == TRUE)
  return (result)
}

saveFile <- paste("BW-",resultDir,args[3],".png",sep="")
png(saveFile, width=2000, height=1000)
v <- ggplot(alteredData, aes(x = genF, y = score, fill = type)) +
  geom_line(aes(y = medianScoreByGeneration, color = type, group = type)) + #, size = 1.5) + 
  geom_boxplot(data = f, width=4.0) + # Filters to only print every so many generations
  #facet_wrap(~type) + # For separate plots
  #ggtitle("INSERT COOL TITLE HERE") +
  ylab("Score") +
  xlab("Generation") +
  theme(
    plot.title = element_text(size=25, face="bold"),
    axis.title.x = element_text(size=25, face="bold"),
    axis.text.x = element_text(size=25, face="bold"),
    axis.title.y = element_text(size=25, face="bold"),
    axis.text.y = element_text(size=25, face="bold"),
    legend.title = element_text(size=25, face="bold"),
    legend.text = element_text(size=25, face="bold"),
    legend.position = c(0.8, 0.2)
  )
print(v)
dev.off()

print("Success!")
print(paste("File saved in ",getwd(),"/",saveFile,sep=""))