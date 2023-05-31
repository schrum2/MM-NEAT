set term pdf enhanced
unset key
set yrange [0:1000]
set xrange [0:125]
set title "MissileMinecraft-BiggerVectorPistonOrientation1 Archive Performance"
set output "MissileMinecraft-BiggerVectorPistonOrientation1_MAPElites_log.pdf"
plot "MissileMinecraft-BiggerVectorPistonOrientation1_MAPElites_log.txt" matrix every ::1 with image
