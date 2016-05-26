set terminal pdf color
set style data lines
set xlabel "Generation"

set output "sunset1-BDDS0_BD-Behavioral Diversity.pdf"
set title "sunset1-BDDS0_BD Behavioral Diversity"
plot \
"sunset1-BDDS0_BD_log.txt" u 1:2 t "MIN", \
"sunset1-BDDS0_BD_log.txt" u 1:3 t "AVG", \
"sunset1-BDDS0_BD_log.txt" u 1:4 t "MAX"

