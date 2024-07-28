#!/usr/bin/env Rscript
library(ggplot2)
library(tidyr)
library(plyr)
library(dplyr)

# Place this in the molecules directory after evolving results, and run a command like:
# Rscript.exe .\AveragesWithErrorBars.R Melting200Boiling400

#base <- "Melting147Boiling336"

# Retrieve command line arguments
args <- commandArgs(trailingOnly = TRUE)

# Check if the base argument is provided
if (length(args) < 1) {
  stop("Error: 'base' argument is missing. Please provide the base parameter as the first argument.")
}

base <- args[1]

# Determine the different experimental conditions
types <- list("Elitism", "AtomTypeCount", "AtomBondCombo", "AtomBranchBondCombo")

# Initialize empty data
evolutionData <- data.frame(generation = integer(), score = double())
# Each experimental condition

type <- "Elitism"
firstPattern <- paste0("^",base,type,"\\d*")
directories <- list.files(".",pattern=firstPattern)

for(d in directories) {
    print(paste("Process",d))
    # Read each individual file
    temp <- read.table(file = paste(d,"/","Molecules","-",d,"_parents_log.txt", sep = ""), sep = '\t', header = FALSE)
    # Rename relevant column
    colnames(temp)[4] <- "score"
    # Add data
    evolutionData <- rbind(evolutionData, data.frame(generation = temp$V1, 
                                       type = type,
                                       run = substring(d,nchar(paste0(base,type))+1), # Get the number following the type
                                       score = c(temp[4])))
}

type <- "AtomTypeCount"
directories <- list.files(".",pattern=paste("^",base,type,"\\d*", sep = ""))
for(d in directories) {
    print(paste("Process",d))
    # Read each individual file
    temp <- read.table(file = paste(d,"/","Molecules","-",d,"_Fill_log.txt", sep = ""), sep = '\t', header = FALSE)
    # Rename relevant column
    colnames(temp)[4] <- "score"
    # Add data
    evolutionData <- rbind(evolutionData, data.frame(generation = temp$V1 / 10, # Scale to match with objective evolution
                                       type = type,
                                       run = substring(d,nchar(paste0(base,type))+1), # Get the number following the type
                                       score = c(temp[4])))
}


type <- "AtomBondCombo"
directories <- list.files(".",pattern=paste("^",base,type,"\\d*", sep = ""))
for(d in directories) {
    print(paste("Process",d))
    # Read each individual file
    temp <- read.table(file = paste(d,"/","Molecules","-",d,"_Fill_log.txt", sep = ""), sep = '\t', header = FALSE)
    # Rename relevant column
    colnames(temp)[4] <- "score"
    # Add data
    evolutionData <- rbind(evolutionData, data.frame(generation = temp$V1 / 10, # Scale to match with objective evolution
                                       type = type,
                                       run = substring(d,nchar(paste0(base,type))+1), # Get the number following the type
                                       score = c(temp[4])))
}


type <- "AtomBranchBondCombo"
directories <- list.files(".",pattern=paste("^",base,type,"\\d*", sep = ""))
for(d in directories) {
    print(paste("Process",d))
    # Read each individual file
    temp <- read.table(file = paste(d,"/","Molecules","-",d,"_Fill_log.txt", sep = ""), sep = '\t', header = FALSE)
    # Rename relevant column
    colnames(temp)[4] <- "score"
    # Add data
    evolutionData <- rbind(evolutionData, data.frame(generation = temp$V1 / 2, # Scale to match with objective evolution (scale different from other MAP Elites)
                                       type = type,
                                       run = substring(d,nchar(paste0(base,type))+1), # Get the number following the type
                                       score = c(temp[4])))
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
  
minFitness <- -15.0

saveFile <- paste("AVG-",base,".png",sep="")
png(saveFile, width=2000, height=1000)
v <- ggplot(evolutionStats, aes(x = generation, y = avgScore, color = type)) +
  geom_ribbon(aes(ymin = lowScore, ymax = highScore, fill = type), alpha = 0.05, show.legend = FALSE) +
  geom_line(size = 1.5) + 
  # Should the 10 here be a parameter? Controls frequency of point plotting. Change size too?
  geom_point(data = subset(evolutionStats, generation %% 10 == 0), size = 10, aes(shape = type)) + 
  # This can be adapted to indicate significant pairwise differences.
  # However, some work needs to be done to make sure testData compares the relevant cases
  #geom_point(data = testData, 
  #           aes(x = generation, 
  #               y = if_else(significant, -spacePerComparison*match(type, comparisonList), -100000), 
  #               size = 5, color = type, shape = type), 
  #           alpha = 0.5, show.legend = FALSE) +
  # For separate plots
  #facet_wrap(~type) + 
  ggtitle("Fitness-based Evolution vs. Quality Diversity") +
  coord_cartesian(ylim=c(minFitness,0.0), xlim=c(0,49)) +
  scale_color_discrete(breaks=types) +
  guides(size = FALSE, alpha = FALSE) +
  ylab("Average Score") +
  xlab("1000 Evaluations") +
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