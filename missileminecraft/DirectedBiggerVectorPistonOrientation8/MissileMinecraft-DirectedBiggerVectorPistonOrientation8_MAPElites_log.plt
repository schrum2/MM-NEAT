unset key
set yrange [0:1000]
set xrange [0:125]
set title "MissileMinecraft-DirectedBiggerVectorPistonOrientation8 Archive Performance"
plot "MissileMinecraft-DirectedBiggerVectorPistonOrientation8_MAPElites_log.txt" matrix every ::1 with image
