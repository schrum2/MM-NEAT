#!/usr/bin/env Rscript
library(ggplot2)
library(tidyr)
library(plyr)
library(dplyr)

args = commandArgs(trailingOnly=TRUE)

if (length(args)==0) {
  stop("Specify name of result directory as command line parameter.", call.=FALSE)
} 
# Set working directory and move into it
resultDir <- args[1]
setwd(paste("./",resultDir,sep=""))
# Determine the different experimental conditions
types <- unique(sub("\\d+","",list.files(".",pattern="[a-zA-Z]+\\d+")))
# Initialize empty data
evolutionData <- data.frame(generation = integer(), score = double())
# Exach experimental condition
for(t in types) {
  # Get each directory starting with the type name, followed by digits
  directories <- list.files(".",pattern=paste("^",t,"\\d*", sep = ""))
  for(d in directories) {
    # Read each individual file
    temp <- read.table(file = paste(d,"/RL-",d,"_parents_log.txt", sep = ""), sep = '\t', header = FALSE)
    evolutionData <- rbind(evolutionData, data.frame(generation = temp$V1, 
                                       type = paste(t,sep=""),
                                       run = substring(d,nchar(t)+1), # Get the number following the type
                                       score = temp$V4))
  }
}

# Extract states: mean, lower confidence bound, upper confidence bound
evolutionStats <- evolutionData %>%
  group_by(type, generation) %>%
  summarize(n = length(run), avgScore = mean(score), stdevScore = sd(score)) %>%
  mutate(stderrScore = qt(0.975, df = n - 1)*stdevScore/sqrt(n)) %>%
  mutate(lowScore = avgScore - stderrScore, highScore = avgScore + stderrScore)

png(paste(resultDir,".png",sep=""), width=2000, height=1000)
v <- ggplot(evolutionStats, aes(x = generation, y = avgScore, color = type)) +
  geom_ribbon(aes(ymin = lowScore, ymax = highScore, fill = type, alpha = 0.05)) +
  geom_line(size = 1.5) + 
  #facet_wrap(~type) + # For separate plots
  #ggtitle("INSERT COOL TITLE HERE") +
  ylab("Average Score") +
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
print(paste("File saved in ",getwd(),"/",resultDir,".png"), sep="")