unset key
set yrange [0:600]
set xrange [0:125]
set title "MinecraftInteger-MEObserverIntegerPistonOrientation1 Archive Performance"
plot "MinecraftInteger-MEObserverIntegerPistonOrientation1_MAPElites_log.txt" matrix every ::1 with image
