set term pdf enhanced
unset key
set yrange [0:600]
set xrange [0:702]
set title "MinecraftInteger-MEObserverIntegerCountNegative1 Archive Performance"
set output "MinecraftInteger-MEObserverIntegerCountNegative1_MAPElites_log.pdf"
plot "MinecraftInteger-MEObserverIntegerCountNegative1_MAPElites_log.txt" matrix every ::1 with image
