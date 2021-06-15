""" 2D MAP-Elites archive plotter (Only for 2D archives with equal amount of bins in both dimensions)
    
    Usage:
    python 2D_bin_plotter.py <plot file to display>
    python 2D_bin_plotter.py ...\MM-NEAT\mapelitesfunctionoptimization\MAPElitesSphereFunctionOptimization20\mapelitesfunctionoptimization-MAPElitesSphereFunctionOptimization20_MAPElites_log.txt
    
"""
import numpy as np
import matplotlib.pyplot as plt
import sys
import math

try: # Get the file path from arguments
    file_path = sys.argv[1]
    print("File input: " + file_path)
except:
    print("File should be specified as argument!")
    quit()
 
try: # Get file itself
    opened_file = open(file_path, "r")
    for line in opened_file: # iterate through lines
        pass
    last_line = line # get last one
    file_contents = last_line.split("\t")[1:] # Split last line into 1D bins
    dir = file_path[:file_path.rfind("\\")+1]
    title = file_path[file_path.rfind("\\")+1:file_path.rfind("_log.txt")]
except:
    print("File could not be opened.")
    quit()
    
def to_number(string_in): # Function to convert strings into numbers
    if string_in == "-Infinity":
        return np.NINF
    else:
        return float(string_in)

numeric_contents = [to_number(i) for i in file_contents] # Strings to Floats

bins = np.array(numeric_contents) # To array
dim_size = math.floor(math.sqrt(len(numeric_contents)))
bins.resize(dim_size, dim_size) # Resize 1D array to 2D array with dimensions based on the overall size (must be square)


plt.text(dim_size/2, -(dim_size/20), title, horizontalalignment='center', verticalalignment='baseline')
plt.imshow(bins)

plt.savefig(dir+title+".png")


plt.show() # Show bins