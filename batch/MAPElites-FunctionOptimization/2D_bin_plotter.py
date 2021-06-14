import numpy as np
import matplotlib.pyplot as plt
import sys
import math

try:
    file_path = sys.argv[1]
    print("File input: " + file_path)
except:
    print("File should be specified as argument!")
    quit()
 
try:
    opened_file = open(file_path, "r")
    for line in opened_file:
        pass
    last_line = line
    file_contents = last_line.split("\t")[1:]
except:
    print("File could not be opened.")
    quit()
    
def to_number(string_in):
    if string_in == "-Infinity":
        return -400
    else:
        return float(string_in)

numeric_contents = [to_number(i) for i in file_contents]

bins = np.array(numeric_contents)
bins.resize(math.floor(math.sqrt(len(numeric_contents))), math.floor(math.sqrt(len(numeric_contents))))

plt.imshow(bins)
plt.show()

with open('filename.txt') as f:
    for line in f:
        pass
    last_line = line