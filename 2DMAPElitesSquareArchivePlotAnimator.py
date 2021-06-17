""" 2D MAP-Elites archive plotter (Only for 2D archives with equal amount of bins in both dimensions)
    
    Usage:
    python 2DMAPElitesSquareArchivePlotAnimator.py <plot file to display> <first dimension name> <first dimension size> <second dimension name> <second dimension size> <logging frequency>
    python 2DMAPElitesSquareArchivePlotAnimator.py latentvariablepartition/Mario0/LatentVariablePartition-Mario0_MAPElites_log.txt "Slice 1" 100 "Slice 2" 100 10
    
"""
import numpy as np
import matplotlib.pyplot as plt
import sys
import math
from pathlib import Path
from matplotlib import colors
import glob
from PIL import Image

try: # Get the file path from arguments
    file_path = sys.argv[1]
    print("File input: " + file_path)
except:
    print("File should be specified as argument!")
    quit()
 
try: # Get file itself
    opened_file = open(file_path, "r")
    lines = []
    for line in opened_file: # iterate through lines
        lines.append(line.split("\t")[1:])
    dir = file_path[:file_path.rfind("/")+1]
    title = file_path[file_path.rfind("/")+1:file_path.rfind("_log.txt")]
except:
    print("File could not be opened.")
    quit()

try:
    dimension_names = [sys.argv[2], sys.argv[4]]
    dimensions = [int(sys.argv[3]), int(sys.argv[5])]
except:
    print("Dimensions were not specified!")
    quit()

try:
    logging_frequeny = int(sys.argv[6])
except:
    print("Logging frequency was not specified, defaulting to 1")
    logging_frequeny = 1

def to_number(string_in): # Function to convert strings into numbers
    if string_in == "-Infinity":
        return np.NINF
    else:
        return float(string_in)

vmin = float("inf")
vmax = float("-inf")

numeric_lines = []
for line in lines:
    numeric_contents = [] # Strings to Floats
    for string_in in line:
        if "-Infinity" in string_in or "X" in string_in  :
            numeric_contents.append(np.NINF)
        else:
            temp_value = float(string_in)
            if vmin > temp_value and not math.isinf(temp_value):
                vmin = temp_value
            if vmax < temp_value and not math.isinf(temp_value):
                vmax = temp_value
            numeric_contents.append(temp_value)
    numeric_lines.append(numeric_contents)

norm = colors.Normalize(vmin=vmin, vmax=vmax) # normalize colors

Path(dir+"archive_animated/").mkdir(parents=True, exist_ok=True)

print("Finished reading file, outputting images...")
for iteration in range(len(numeric_lines)):
    if iteration % logging_frequeny == 0:
        bins = np.array(numeric_lines[iteration]) # To array
        bins.resize(dimensions[0], dimensions[1]) # Resize 1D array to 2D array with dimensions based on the overall size (must be square)

        plt.text(dimensions[1]/2, (dimensions[0]/20)+dimensions[0], (title + " Step:"+str(iteration)), horizontalalignment='center', verticalalignment='baseline')
        plt.xlabel(dimension_names[0])
        plt.ylabel(dimension_names[1])
        plt.xlim(left=0.0, right=dimensions[0])
        plt.ylim(bottom=0.0, top=dimensions[1])

        plt.imshow(bins, norm=norm)
        
        plt.savefig(dir+"archive_animated/"+title+(str(iteration).zfill(len(str(len(numeric_lines)))))+".png")
        plt.clf()

print("Finished outputting images, creating GIF...")

# filepaths
fp_in = dir+"archive_animated/"+title+"*.png"
fp_out = dir+"archive_animated/"+title+"_archive.gif"

img, *imgs = [Image.open(f) for f in sorted(glob.glob(fp_in))]
img.save(fp=fp_out, format='GIF', append_images=imgs,
         save_all=True, duration=200, loop=0)
     
print("All done!")