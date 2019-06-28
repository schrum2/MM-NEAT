import argparse
import os

parser = argparse.ArgumentParser()
parser.add_argument('file_name', type=str, help="File name and directory")

args = parser.parse_args()

level_array = []
file_name = args.file_name

if not os.path.exists(file_name):
    os.makedirs(file_name)

with open(file_name + '.txt') as file:
    for line in file:
        row = []
        for ch in line:
            if ch is not '\n':
                row.append(ch)
        level_array.append(row)

w = 16
h = 11
lW = int(len(level_array[0]) / w)
lH = int(len(level_array) / h)
file_count = 0

for x in range(lW):
    for y in range(lH): 
        if(level_array[y*h][x*w] == '-'):
            continue

        file = open(file_name + '/' + str(file_count) + '.txt', 'w')
        for i in range(h):
            for j in range(w):
                file.write(level_array[i + y*h][j + x*w])
            file.write('\n')

        file.close()
        file_count += 1
