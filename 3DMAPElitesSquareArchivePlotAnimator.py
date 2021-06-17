""" 2D MAP-Elites archive plotter (Only for 2D archives with equal amount of bins in both dimensions)
    
    Usage:
    python 3DMAPElitesSquareArchivePlotAnimator.py <plot file to display> <first dimension name> <first dimension size> <second dimension name> <second dimension size> <third dimension name> <third dimension size> <row amount> <logging frequency> <max value> <min value>
    python 3DMAPElitesSquareArchivePlotAnimator.py zeldadungeonswallwaterrooms/ME0/ZeldaDungeonsWallWaterRooms-ME0_MAPElites_log.txt "Wall Tile Percent" 10 "Water Tile Percent" 10 "Reachable Rooms" 26 2 5 1.0 0.0
    
    Note: Min and Max do NOT need to be given, they will be calculated automatically
"""
import numpy as np
import matplotlib.pyplot as plt
import sys
import math
from pathlib import Path
from matplotlib import colors, cm
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
    dimension_names = [sys.argv[2], sys.argv[4], sys.argv[6]]
    dimensions = [int(sys.argv[3]), int(sys.argv[5]), int(sys.argv[7])]
except:
    print("Dimensions were not specified!")
    quit()

try:
    rows = int(sys.argv[8])
except:
    print("The number of rows was not specified!")
    quit()

try:
    logging_frequeny = int(sys.argv[9])
except:
    print("Logging frequency was not specified, defaulting to 1")
    logging_frequeny = 1
    
try:
    calc_minmax = False
    vmax = float(sys.argv[10])
    vmin = float(sys.argv[11])
    print("Min and Max specified as: ("+str(vmin)+", "+str(vmax)+")")
except:
    print("Min and/or Max not specified, will be calculated")
    calc_minmax = True
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
            numeric_contents.append(temp_value)
            if calc_minmax:
                if vmin > temp_value and not math.isinf(temp_value):
                    vmin = temp_value
                if vmax < temp_value and not math.isinf(temp_value):
                    vmax = temp_value
    numeric_lines.append(numeric_contents)

norm = colors.Normalize(vmin=vmin, vmax=vmax) # normalize colors

Path(dir+"archive_animated/").mkdir(parents=True, exist_ok=True)

if calc_minmax:
    print("Calculated min and max values: ("+str(vmin)+", "+str(vmax)+")")
    
print("Finished reading file, outputting images...")  
for iteration in range(len(numeric_lines)):
    if iteration % logging_frequeny == 0:
    
        archive_slices = []
        slice_size = dimensions[2] * dimensions[1]
        for i in range(dimensions[0]):
            archive_slices.append(numeric_lines[iteration][(i*slice_size):((i+1)*slice_size)])
    
        archive_slice_arrays = [np.array(slice) for slice in archive_slices]

        for slice in archive_slice_arrays:
            slice.resize(dimensions[1], dimensions[2]) # Resize 1D array to 2D array with dimensions based on the overall size
            
        cmap = "viridis" # Colormap to use

        columns = math.ceil(dimensions[0]/rows)

        fig, axs = plt.subplots(nrows=rows, ncols=columns, constrained_layout=True, figsize=(columns*4, rows*3))
        
        fig.colorbar(cm.ScalarMappable(norm=norm, cmap=cmap), ax=axs[:, :], location='right', aspect=50)
        
        end = rows*columns
        while end > dimensions[0]:
            fig.delaxes(axs[rows-1, (columns - (rows*columns % end) - 1)])
            end -= 1

        fig.suptitle(title + " Step:"+str(iteration))

        counter = 0
        for ax, slice in zip(axs.flat, archive_slice_arrays):
            ax.imshow(slice, extent=[0, dimensions[2], dimensions[1], 0], cmap=cmap, norm=norm)
            ax.set_ylim(bottom=0.0, top=dimensions[1])
            ax.set_xlim(left=0.0, right=dimensions[2])
            ax.set_xlabel(dimension_names[2])
            ax.set_ylabel(dimension_names[1])
            ax.set_title(dimension_names[0]+": "+str(counter))
            counter+=1
       
        
        plt.savefig(dir+"archive_animated/"+title+(str(iteration).zfill(len(str(len(numeric_lines)))))+".png")
        plt.clf()
        plt.cla()
    

print("Finished outputting images, creating GIF...")

# filepaths
fp_in = dir+"archive_animated/"+title+"*.png"
fp_out = dir+"archive_animated/"+title+"_archive.gif"

img, *imgs = [Image.open(f) for f in sorted(glob.glob(fp_in))]
img.save(fp=fp_out, format='GIF', append_images=imgs,
         save_all=True, duration=200, loop=0)
     
print("All done!")