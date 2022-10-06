""" 2D MAP-Elites archive plotter (Only for 2D archives with equal amount of bins in both dimensions)
    
    Usage:
    python 3DMAPElitesSquareArchivePlotter.py <plot file to display> <plot display title> <first dimension name> <first dimension size> <second dimension name> <second dimension size> <third dimension name> <third dimension size> <row amount> <max value> <min value>
    python 3DMAPElitesSquareArchivePlotter.py zeldadungeonswallwaterrooms/ME0/ZeldaDungeonsWallWaterRooms-ME0_MAPElites_log.txt "Plot" "Wall Tile Percent" 10 "Water Tile Percent" 10 "Reachable Rooms" 26 2 1.0 0.0
    
    Note: Min and Max do NOT need to be given, they will be calculated automatically
"""
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.patches as patches
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
    dimension_names = [sys.argv[3], sys.argv[5], sys.argv[7]]
    dimensions = [int(sys.argv[4]), int(sys.argv[6]), int(sys.argv[8])]
except:
    print("Dimensions were not specified!")
    quit()


try: # Get number of rows
    rows = int(sys.argv[9])
except:
    print("The number of rows was not specified!")
    quit()
    
try: # Get min and max
    calc_minmax = False
    vmax = float(sys.argv[10])
    vmin = float(sys.argv[11])
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

archive_slices = []
slice_size = dimensions[2] * dimensions[1]
for i in range(dimensions[0]):
    archive_slices.append(numeric_contents[(i*slice_size):((i+1)*slice_size)])

norm = colors.Normalize(vmin=vmin, vmax=vmax) # normalize colors
    
archive_slice_arrays = [np.array(slice) for slice in archive_slices]

for slice in archive_slice_arrays:
    slice.resize(dimensions[1], dimensions[2]) # Resize 1D array to 2D array with dimensions based on the overall size

cmap = "viridis" # Colormap to use

columns = math.ceil(dimensions[0]/rows)

fig, axs = plt.subplots(nrows=rows, ncols=columns, constrained_layout=True, figsize=(columns*4, rows*3)) # Make subplots

fig.colorbar(cm.ScalarMappable(norm=norm, cmap=cmap), ax=axs[:, :], location='right', aspect=50) # Make colorbar

end = rows*columns
while end > dimensions[0]:
    fig.delaxes(axs[rows-1, (columns - (rows*columns % end) - 1)])
    end -= 1

fig.suptitle(plot_title)

xmin = float("inf")
ymin = float("inf")
zmin = float("inf")

xmax = float("-inf")
ymax = float("-inf")
zmax = float("-inf")

# The restricted range was specified at the command line.
# Will draw the red box in a certain place, even if some
# bins outside of it are occupied
if len(sys.argv) > 12:
    xmin = int(sys.argv[12])
    ymin = int(sys.argv[13])
    zmin = int(sys.argv[14])

    xmax = int(sys.argv[15])
    ymax = int(sys.argv[16])
    zmax = int(sys.argv[17])
else:
    # Figure out where to place red outline
    for x in range(0,len(archive_slice_arrays)):
        for y in range(0,len(archive_slice_arrays[x])):
            for z in range(0,len(archive_slice_arrays[x][y])):
                if archive_slice_arrays[x][y][z] > float("-inf"):
                    xmin = min(xmin,x)
                    ymin = min(ymin,y)
                    zmin = min(zmin,z)

                    xmax = max(xmax,x)
                    ymax = max(ymax,y)
                    zmax = max(zmax,z)

print(f"{xmin} {xmax}")
print(f"{ymin} {ymax}")
print(f"{zmin} {zmax}")

counter = 0
for ax, slice in zip(axs.flat, archive_slice_arrays):
    ax.imshow(slice, extent=[0, dimensions[2], dimensions[1], 0], norm=norm, cmap=cmap)
    # Box occupied area if box contains data
    if counter >= xmin and counter <= xmax:
        # Create a Rectangle patch
        rect = patches.Rectangle((zmin, ymin), zmax - zmin + 1, ymax - ymin + 1, linewidth=1, edgecolor='r', facecolor='none')
        # Add the patch to the Axes
        ax.add_patch(rect)
    
    ax.set_ylim(bottom=0.0, top=dimensions[1])
    ax.set_xlim(left=0.0, right=dimensions[2])
    ax.set_xlabel(dimension_names[2]) # Add labels
    ax.set_ylabel(dimension_names[1])
    ax.set_title(dimension_names[0]+": "+str(counter))
    counter+=1

if len(sys.argv) > 18:
    suffix = sys.argv[18]
else:
    suffix=""

plt.savefig(dir+title+suffix+".png", dpi=1000) # Save file, DPI can be specified, determines resolution of output image
plt.savefig(dir+title+suffix+".pdf", dpi=1000) # Save file, DPI can be specified, determines resolution of output image


plt.show() # Show bins in window






