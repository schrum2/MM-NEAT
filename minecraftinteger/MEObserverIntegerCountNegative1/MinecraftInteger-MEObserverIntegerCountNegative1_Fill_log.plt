set term pdf enhanced
set key bottom right
set xrange [0:600]
set title "MinecraftInteger-MEObserverIntegerCountNegative1 Archive Filled Bins"
set output "MinecraftInteger-MEObserverIntegerCountNegative1_FillWithDiscarded_log.pdf"
plot "MinecraftInteger-MEObserverIntegerCountNegative1_Fill_log.txt" u 1:2 w linespoints t "Total", \
     "MinecraftInteger-MEObserverIntegerCountNegative1_Fill_log.txt" u 1:5 w linespoints t "Discarded"
set title "MinecraftInteger-MEObserverIntegerCountNegative1 Archive Filled Bins Percentage"
set output "MinecraftInteger-MEObserverIntegerCountNegative1_FillPercentage_log.pdf"
plot "MinecraftInteger-MEObserverIntegerCountNegative1_Fill_log.txt" u 1:($2 / 702) w linespoints t "Total"
set title "MinecraftInteger-MEObserverIntegerCountNegative1 Archive Filled Bins"
set output "MinecraftInteger-MEObserverIntegerCountNegative1_Fill_log.pdf"
plot "MinecraftInteger-MEObserverIntegerCountNegative1_Fill_log.txt" u 1:2 w linespoints t "Total", \
     "MinecraftInteger-MEObserverIntegerCountNegative1_Fill_log.txt" u 1:6 w linespoints t "Restricted"
set title "MinecraftInteger-MEObserverIntegerCountNegative1 Archive QD Scores"
set output "MinecraftInteger-MEObserverIntegerCountNegative1_QD_log.pdf"
plot "MinecraftInteger-MEObserverIntegerCountNegative1_Fill_log.txt" u 1:3 w linespoints t "QD Score", \
     "MinecraftInteger-MEObserverIntegerCountNegative1_Fill_log.txt" u 1:7 w linespoints t "Restricted QD Score"
set title "MinecraftInteger-MEObserverIntegerCountNegative1 Maximum individual fitness score
set output "MinecraftInteger-MEObserverIntegerCountNegative1_Maximum_log.pdf"
plot "MinecraftInteger-MEObserverIntegerCountNegative1_Fill_log.txt" u 1:4 w linespoints t "Maximum Fitness Score", \
     "MinecraftInteger-MEObserverIntegerCountNegative1_Fill_log.txt" u 1:8 w linespoints t "Restricted Maximum Fitness Score"
