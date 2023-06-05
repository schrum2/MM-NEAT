set style data lines
set xlabel "Generation"

set title "MinecraftInteger-ESObserverInteger2_parents ChangeCenterOfMassFitness"
plot \
"MinecraftInteger-ESObserverInteger2_parents_log.txt" u 1:2 t "MIN", \
"MinecraftInteger-ESObserverInteger2_parents_log.txt" u 1:3 t "AVG", \
"MinecraftInteger-ESObserverInteger2_parents_log.txt" u 1:4 t "MAX"

pause -1

