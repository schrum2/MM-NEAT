set term pdf enhanced
set key bottom right
set xrange [0:600]
set title "MinecraftInteger-MEObserverIntegerPistonOrientation1 Archive Filled Bins"
set output "MinecraftInteger-MEObserverIntegerPistonOrientation1_FillWithDiscarded_log.pdf"
plot "MinecraftInteger-MEObserverIntegerPistonOrientation1_Fill_log.txt" u 1:2 w linespoints t "Total", \
     "MinecraftInteger-MEObserverIntegerPistonOrientation1_Fill_log.txt" u 1:5 w linespoints t "Discarded"
set title "MinecraftInteger-MEObserverIntegerPistonOrientation1 Archive Filled Bins Percentage"
set output "MinecraftInteger-MEObserverIntegerPistonOrientation1_FillPercentage_log.pdf"
plot "MinecraftInteger-MEObserverIntegerPistonOrientation1_Fill_log.txt" u 1:($2 / 125) w linespoints t "Total"
set title "MinecraftInteger-MEObserverIntegerPistonOrientation1 Archive Filled Bins"
set output "MinecraftInteger-MEObserverIntegerPistonOrientation1_Fill_log.pdf"
plot "MinecraftInteger-MEObserverIntegerPistonOrientation1_Fill_log.txt" u 1:2 w linespoints t "Total", \
     "MinecraftInteger-MEObserverIntegerPistonOrientation1_Fill_log.txt" u 1:6 w linespoints t "Restricted"
set title "MinecraftInteger-MEObserverIntegerPistonOrientation1 Archive QD Scores"
set output "MinecraftInteger-MEObserverIntegerPistonOrientation1_QD_log.pdf"
plot "MinecraftInteger-MEObserverIntegerPistonOrientation1_Fill_log.txt" u 1:3 w linespoints t "QD Score", \
     "MinecraftInteger-MEObserverIntegerPistonOrientation1_Fill_log.txt" u 1:7 w linespoints t "Restricted QD Score"
set title "MinecraftInteger-MEObserverIntegerPistonOrientation1 Maximum individual fitness score
set output "MinecraftInteger-MEObserverIntegerPistonOrientation1_Maximum_log.pdf"
plot "MinecraftInteger-MEObserverIntegerPistonOrientation1_Fill_log.txt" u 1:4 w linespoints t "Maximum Fitness Score", \
     "MinecraftInteger-MEObserverIntegerPistonOrientation1_Fill_log.txt" u 1:8 w linespoints t "Restricted Maximum Fitness Score"
