set terminal pdf color
set style data lines
set xlabel "Generation"

set output "sunset1-BDDS0_parents-Error.pdf"
set title "sunset1-BDDS0_parents Error"
plot \
"sunset1-BDDS0_parents_log.txt" u 1:2 t "MIN", \
"sunset1-BDDS0_parents_log.txt" u 1:3 t "AVG", \
"sunset1-BDDS0_parents_log.txt" u 1:4 t "MAX"

