""" 2D MAP-Elites archive plotter (Only for 2D archives with equal amount of bins in both dimensions)
    
    Usage:
    python 2D_bin_plotter.py <plot file to display> <first dimension name> <first dimension size> <second dimension name> <second dimension size> <third dimension name> <third dimension size>
    python 2D_bin_plotter.py ...\MM-NEAT\mapelitesfunctionoptimization\MAPElitesSphereFunctionOptimization20\mapelitesfunctionoptimization-MAPElitesSphereFunctionOptimization20_MAPElites_log.txt
    
"""
import numpy as np
import matplotlib.pyplot as plt
import sys
import math
from matplotlib import colors

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
    dir = file_path[:file_path.rfind("/")+1]
    title = file_path[file_path.rfind("/")+1:file_path.rfind("_log.txt")]
except:
    print("File could not be opened.")
    quit()
    
try:
    dimension_names = [sys.argv[2], sys.argv[4], sys.argv[6]]
    dimensions = [int(sys.argv[3]), int(sys.argv[5]), int(sys.argv[7])]
except:
    print("Dimensions were not specified!")
    quit()


vmin = float("inf")
vmax = float("-inf")

numeric_contents = [] # Strings to Floats
for string_in in file_contents:
    if string_in == "-Infinity":
        numeric_contents.append(np.NINF)
    else:
        temp_value = float(string_in)
        if vmin > temp_value and not math.isinf(temp_value):
            vmin = temp_value
        if vmax < temp_value and not math.isinf(temp_value):
            vmax = temp_value
        numeric_contents.append(temp_value)

archive_slices = []
slice_size = dimensions[2] * dimensions[1]
for i in range(dimensions[0]):
    archive_slices.append(numeric_contents[(i*slice_size):((i+1)*slice_size)])

norm = colors.Normalize(vmin=vmin, vmax=vmax) # normalize colors
    
archive_slice_arrays = [np.array(slice) for slice in archive_slices]

for slice in archive_slice_arrays:
    slice.resize(dimensions[1], dimensions[2]) # Resize 1D array to 2D array with dimensions based on the overall size

fig, axs = plt.subplots(nrows=2, ncols=math.ceil(dimensions[0]/2), tight_layout=True, figsize=(18, 8))

if dimensions[0] % 2 == 1:
    fig.delaxes(axs[1, math.ceil(dimensions[0]/2)-1])

fig.suptitle(title, x=0.5, y=1  )

counter = 0
for ax, slice in zip(axs.flat, archive_slice_arrays):
    ax.imshow(slice, extent=[0, dimensions[2], dimensions[1], 0], norm=norm)
    ax.set_ylim(bottom=0.0, top=dimensions[1])
    ax.set_xlim(left=0.0, right=dimensions[2])
    ax.set_xlabel(dimension_names[2])
    ax.set_ylabel(dimension_names[1])
    ax.set_title(dimension_names[0]+": "+str(counter))
    counter+=1

plt.savefig(dir+title+".png")


plt.show() # Show bins





