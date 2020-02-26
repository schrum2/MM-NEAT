#!/usr/bin/env Rscript
args = commandArgs(trailingOnly=TRUE)
if (length(args)==0) {
  stop("Supply Zelda or Mario", call.=FALSE)
}

#setwd("E:\\Users\\he_de\\workspace\\GameGAN")

game <- args[1]
#game <- "Zelda"

library(ggplot2)
library(tidyr)
library(plyr)
library(dplyr)
library(stringr)

#setwd(paste("./",resultDir,sep=""))
# Get log prefix
scoreIndex <- 2
# Determine the different experimental conditions
types <- list(paste(game,"GAN",sep=""),paste(game,"CPPNtoGAN",sep=""))
# Initialize empty data
evolutionData <- data.frame(generation = integer(), score = double())
# Exach experimental condition
for(t in types) {
  # Get each directory starting with the type name, followed by digits
  directories <- list.files(paste("./",tolower(t),"/", sep=""),pattern=paste("^","MAPElites","\\d*", sep = ""))
  for(d in directories) {
    # Read each individual file
    temp <- read.table(file = paste("./",tolower(t),"/",d,"/",t,"-",d,"_Fill_log.txt", sep = ""), sep = '\t', header = FALSE)
    # Rename relevant column
    colnames(temp)[scoreIndex] <- "score"
    
    typeLabel <- str_replace(t,"CPPNtoGAN","CPPN2GAN")
    typeLabel <- str_replace(typeLabel,"MarioGAN","Direct2GAN")
    typeLabel <- str_replace(typeLabel,"ZeldaGAN","Direct2GAN")
    typeLabel <- str_replace(typeLabel,"Mario","")
    typeLabel <- str_replace(typeLabel,"Zelda","")
    
    # Add data
    evolutionData <- rbind(evolutionData, data.frame(generation = temp$V1, 
                                       type = paste(typeLabel,sep=""),
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
# Not needed for CPPN2GAN vs Direct2GAN because the differences are so clear.
evolutionStats <- evolutionData %>%
  group_by(type, generation) %>%
  summarize(n = length(run), avgScore = mean(score), stdevScore = sd(score)) %>%
  mutate(stderrScore = qt(0.975, df = n - 1)*stdevScore/sqrt(n)) %>%
  mutate(lowScore = avgScore - stderrScore, highScore = avgScore + stderrScore)

# Configure space at bottom for t-test data
spaceForTests <- maxScore / 6
spacePerComparison <- spaceForTests / length(comparisonList)

evolutionStats$generation <- evolutionStats$generation * 100
  
cbPalette <- c("#E69F00", "#56B4E9", "#009E73", "#F0E442", "#0072B2", "#D55E00", "#CC79A7")

saveFile <- paste("AVG-",game,".pdf",sep="")
#png(saveFile, width=2000, height=1000)
pdf(saveFile, width=4, height=2.5)
v <- ggplot(evolutionStats, aes(x = generation, y = avgScore, color = type)) +
  geom_ribbon(aes(ymin = lowScore, ymax = highScore, fill = type), alpha = 0.05, show.legend = FALSE) +
  geom_line(size = 0.3) + 
  geom_point(data = subset(evolutionStats, generation %% 5000 == 0), 
             size = 2, aes(shape = type)) + 
  #scale_y_continuous(expand = c(0, 0), limits = c(0, NA)) +
  #scale_color_continuous(guide = guide_legend(reverse=TRUE)) +
  guides(shape = guide_legend(reverse=TRUE), color = guide_legend(reverse=TRUE)) +
  #scale_shape_discrete(guide = guide_legend(reverse=TRUE)) +
  #scale_linetype_manual(guide = guide_legend(reverse=TRUE)) +
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
    legend.position = c(0.8, 0.5)
  )
print(v)
dev.off()

print("Success!")
print(paste("File saved in ",getwd(),"/",saveFile,sep=""))