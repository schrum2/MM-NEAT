unset key
set yrange [0:1000]
set xrange [0:125]
set title "MissileMinecraft-DirectedBiggerVectorPistonOrientation99 Archive Performance"
plot "MissileMinecraft-DirectedBiggerVectorPistonOrientation99_MAPElites_log.txt" matrix every ::1 with image
