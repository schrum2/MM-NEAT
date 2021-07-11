""" 2D MAP-Elites archive plotter (Only for 2D archives with equal amount of bins in both dimensions)
    
    Usage:
    python 2DMAPElitesSquareArchivePlotter.py <plot file to display> <plot display title> <first dimension name> <first dimension size> <second dimension name> <second dimension size> <max value> <min value>
    python 2DMAPElitesSquareArchivePlotter.py latentvariablepartition/Mario0/LatentVariablePartition-Mario0_MAPElites_log.txt "Plot" "Slice 1" 100 "Slice 2" 100 420 -1

    Note: Min and Max do NOT need to be given, they will be calculated automatically
"""
import numpy as np
import matplotlib.pyplot as plt
import sys
import math
from matplotlib import colors, cm

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

try: # Get dimensions and relative sizes
    plot_title = sys.argv[2]
except:
    print("Dimensions were not specified!")
    quit()
    
try: # Get dimensions and relative sizes
    dimension_names = [sys.argv[3], sys.argv[5]]
    dimensions = [int(sys.argv[4]), int(sys.argv[6])]
except:
    print("Dimensions were not specified!")
    quit()
    
try: # Get min and max
    calc_minmax = False
    vmax = int(sys.argv[7])
    vmin = int(sys.argv[8])
    print("Min and Max specified as: ("+str(vmin)+", "+str(vmax)+")")
except: # If unspecified, calculate them
    print("Min and/or Max not specified, will be calculated")
    calc_minmax = True
    vmin = float("inf")
    vmax = float("-inf")

numeric_contents = [] # Strings to Floats
for string_in in file_contents:
    if "-Infinity" in string_in or "X" in string_in:
        numeric_contents.append(np.NINF)
    else:
        temp_value = float(string_in)
        numeric_contents.append(temp_value)
        if calc_minmax:
            if vmin > temp_value and not math.isinf(temp_value):
                vmin = temp_value
            if vmax < temp_value and not math.isinf(temp_value):
                vmax = temp_value

if calc_minmax:
    print("Calculated min and max values: ("+str(vmin)+", "+str(vmax)+")")

norm = colors.Normalize(vmin=vmin, vmax=vmax) # normalize colors
cmap = "viridis" # Colormap to use
        
bins = np.array(numeric_contents) # To array
bins.resize(dimensions[0], dimensions[1]) # Resize 1D array to 2D array with dimensions based on the overall size (must be square)


plt.text(dimensions[1]/2, (dimensions[0]/20)+dimensions[0], plot_title, horizontalalignment='center', verticalalignment='baseline')
plt.xlabel(dimension_names[1]) # Add labels
plt.ylabel(dimension_names[0])
plt.xlim(left=0.0, right=dimensions[1])
plt.ylim(bottom=0.0, top=dimensions[0])

plt.colorbar(cm.ScalarMappable(norm=norm, cmap=cmap)) # Make colorbar
    
plt.imshow(bins, norm=norm, cmap=cmap, extent=[0, dimensions[1], dimensions[0], 0])


plt.savefig(dir+title+".png", dpi=1000) # Save file, DPI can be specified, determines resolution of output image
plt.savefig(dir+title+".pdf", dpi=1000) # Save file, DPI can be specified, determines resolution of output image


plt.show() # Show bins in window