level_array = []
file_name = 'tloz1_2'

with open(file_name + '.txt') as file:
    for line in file:
        row = []
        for ch in line:
            if ch is not '\n':
                row.append(ch)
        level_array.append(row)

file = open(file_name + '_flip.txt', 'w')

for i in range(len(level_array[0])):
    for j in range(len(level_array)):
        file.write(level_array[j][i])
    file.write('\n')

file.close()
