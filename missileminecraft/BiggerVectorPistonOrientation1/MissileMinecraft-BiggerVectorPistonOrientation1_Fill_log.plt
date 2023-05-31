set term pdf enhanced
set key bottom right
set xrange [0:1000]
set title "MissileMinecraft-BiggerVectorPistonOrientation1 Archive Filled Bins"
set output "MissileMinecraft-BiggerVectorPistonOrientation1_FillWithDiscarded_log.pdf"
plot "MissileMinecraft-BiggerVectorPistonOrientation1_Fill_log.txt" u 1:2 w linespoints t "Total", \
     "MissileMinecraft-BiggerVectorPistonOrientation1_Fill_log.txt" u 1:5 w linespoints t "Discarded"
set title "MissileMinecraft-BiggerVectorPistonOrientation1 Archive Filled Bins Percentage"
set output "MissileMinecraft-BiggerVectorPistonOrientation1_FillPercentage_log.pdf"
plot "MissileMinecraft-BiggerVectorPistonOrientation1_Fill_log.txt" u 1:($2 / 125) w linespoints t "Total"
set title "MissileMinecraft-BiggerVectorPistonOrientation1 Archive Filled Bins"
set output "MissileMinecraft-BiggerVectorPistonOrientation1_Fill_log.pdf"
plot "MissileMinecraft-BiggerVectorPistonOrientation1_Fill_log.txt" u 1:2 w linespoints t "Total", \
     "MissileMinecraft-BiggerVectorPistonOrientation1_Fill_log.txt" u 1:6 w linespoints t "Restricted"
set title "MissileMinecraft-BiggerVectorPistonOrientation1 Archive QD Scores"
set output "MissileMinecraft-BiggerVectorPistonOrientation1_QD_log.pdf"
plot "MissileMinecraft-BiggerVectorPistonOrientation1_Fill_log.txt" u 1:3 w linespoints t "QD Score", \
     "MissileMinecraft-BiggerVectorPistonOrientation1_Fill_log.txt" u 1:7 w linespoints t "Restricted QD Score"
set title "MissileMinecraft-BiggerVectorPistonOrientation1 Maximum individual fitness score
set output "MissileMinecraft-BiggerVectorPistonOrientation1_Maximum_log.pdf"
plot "MissileMinecraft-BiggerVectorPistonOrientation1_Fill_log.txt" u 1:4 w linespoints t "Maximum Fitness Score", \
     "MissileMinecraft-BiggerVectorPistonOrientation1_Fill_log.txt" u 1:8 w linespoints t "Restricted Maximum Fitness Score"
