unset key
set yrange [0:1000]
set xrange [0:125]
set title "MissileMinecraft-DirectedBiggerVectorPistonOrientation47 Archive Performance"
plot "MissileMinecraft-DirectedBiggerVectorPistonOrientation47_MAPElites_log.txt" matrix every ::1 with image
