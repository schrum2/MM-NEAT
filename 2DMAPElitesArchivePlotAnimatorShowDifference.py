""" 2D MAP-Elites archive plotter (Only for 2D archives with equal amount of bins in both dimensions)
    
    Usage:
    python 2DMAPElitesSquareArchivePlotAnimatorShowDifference.py <plot file to display> <plot display title> <first dimension name> <first dimension size> <second dimension name> <second dimension size> <logging frequency> <max value> <min value> <plot emitters?>
    python 2DMAPElitesSquareArchivePlotAnimatorShowDifference.py latentvariablepartition/Mario0/LatentVariablePartition-Mario0_MAPElites_log.txt "Plot" "Slice 1" 100 "Slice 2" 100 10 420 -1 False

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
    print("Successfully Read File.")
except:
    print("File could not be opened.")
    quit()

try: # Get dimensions and relative sizes
    plot_title = sys.argv[2]
except:
    print("Dimensions were not specified!")
    quit()

try: # Get dimension names and the relative sizes
    dimension_names = [sys.argv[3], sys.argv[5]]
    dimensions = [int(sys.argv[4]), int(sys.argv[6])]
    print("Dimensions specified as: "+str(dimension_names)+" with sizes: "+str(dimensions))
except:
    print("Dimensions were not specified!")
    quit()
      
try: # Get the logging frequency
    logging_frequeny = int(sys.argv[7])
    print("Logging Frequency set to: "+str(logging_frequeny))
except:
    print("Logging frequency was not specified, defaulting to 1")
    logging_frequeny = 1
    
try: # Get the min and max
    calc_minmax = False
    vmax = int(sys.argv[8])
    vmin = int(sys.argv[9])
    edges = [vmax, vmin]
    print("Min and Max specified as: ("+str(min(edges))+", "+str(max(edges))+")")
except: # If unspecified, calculates it
    print("Min and/or Max not specified, will be calculated")
    calc_minmax = True
    vmin = float("inf")
    vmax = float("-inf")

try: # Get the min and max
    emitter_parameter = sys.argv[10]
    if emitter_parameter == "False" or emitter_parameter == "false":
        emitter_parameter = False
    else:
        emitter_parameter = True
except: # If unspecified, calculates it
    print("Emitter parameter not specified, defaulting to True.")
    emitter_parameter = True

emitter_means = []
draw_emitters = False
if emitter_parameter:
    try:
        emitter_log_path = file_path[:file_path.rfind("_log.txt")]
        emitter_log_path = emitter_log_path[:emitter_log_path.rfind("_")] + "_EmitterMeans_log.txt"
        opened_file = open(emitter_log_path, "r")
        for line in opened_file: # iterate through lines
            read_line = line.split("\t")[1:]
            seperated_emitters = []
            for each in read_line:
                seperated_emitters.append([int(val) for val in each.strip("\n").split(" ")])
            emitter_means.append(seperated_emitters)
        draw_emitters = True
        print("Emitter means successfully read.")
    except:
        print("Could not get emitter means from file.")


emitter_symbols = ["o", "x", "^", "s", "P", "v", "D", "*"]
emitter_colors = ["red", "blue", "black", "green"]

numeric_lines = []
for line in lines:
    numeric_contents = [] # Strings to Floats
    for string_in in line:
        if "-Infinity" in string_in or "X" in string_in:
            numeric_contents.append(np.NINF)
        else:
            temp_value = float(string_in)
            numeric_contents.append(temp_value)
            if calc_minmax: # Change min or max if possible
                if vmin > temp_value and not math.isinf(temp_value):
                    vmin = temp_value
                if vmax < temp_value and not math.isinf(temp_value):
                    vmax = temp_value            
    numeric_lines.append(numeric_contents)

norm = colors.Normalize(vmin=vmin, vmax=vmax) # normalize colors

Path(dir+"archive_animated/").mkdir(parents=True, exist_ok=True) # Make directory for output images / gif

if calc_minmax:
    print("Calculated min and max values: ("+str(vmin)+", "+str(vmax)+")")
    
print("Finished reading file, outputting images...")

for iteration in range(len(numeric_lines)): # If will log
    if iteration % logging_frequeny == 0:
        emitter_counter = 0
        bins = np.array(numeric_lines[iteration]) # To array
        bins.resize(dimensions[0], dimensions[1]) # Resize 1D array to 2D array with dimensions based on the overall size (must be square)
        
        cmap = "afmhot" # Colormap to use
        
        plt.colorbar(cm.ScalarMappable(norm=norm, cmap=cmap)) # Add color bar
        plt.text(dimensions[1]/2, (dimensions[0]/20)+dimensions[0], (title + " Step:"+str(iteration)), horizontalalignment='center', verticalalignment='baseline')
        plt.xlabel(dimension_names[1]) # Add labels
        plt.ylabel(dimension_names[0])
        plt.xlim(left=0.0, right=dimensions[1])
        plt.ylim(bottom=0.0, top=dimensions[0])
        
        if draw_emitters:
            for e_step in range(len(emitter_means[iteration])):
                x_values = []
                y_values = []
                counter = 0
                while counter < iteration:
                    x_values.append(emitter_means[counter][e_step][1])
                    counter += logging_frequeny
                    
                counter = 0
                while counter < iteration:
                    y_values.append(emitter_means[counter][e_step][0])
                    counter += logging_frequeny
                    
                for index in (range(5)): # How long history is drawn
                    adjusted_index = len(x_values) - index
                    plt.plot(x_values[adjusted_index:adjusted_index+2], y_values[adjusted_index:adjusted_index+2], color=emitter_colors[emitter_counter%len(emitter_colors)], alpha=(((-index+5)/3)))
                
                if len(x_values) > 0: # Final connection
                    plt.plot([x_values[-1], emitter_means[iteration][e_step][1]], [y_values[-1], emitter_means[iteration][e_step][0]], color=emitter_colors[emitter_counter%len(emitter_colors)], alpha=1)
                
                plt.plot(emitter_means[iteration][e_step][1], emitter_means[iteration][e_step][0], marker=emitter_symbols[math.floor(emitter_counter/len(emitter_colors))], color=emitter_colors[emitter_counter%len(emitter_colors)])
                emitter_counter += 1
            
        plt.imshow(bins, cmap=cmap, norm=norm) # Create image
        if iteration > 0:
            prev_bins = np.array(numeric_lines[iteration-logging_frequeny]) # To array
            prev_bins.resize(dimensions[0], dimensions[1])
            plt.imshow(prev_bins, cmap='gray', norm=norm, extent=[0, dimensions[1], dimensions[0], 0]) # Create image
        
        plt.savefig(dir+"archive_animated/"+title+(str(iteration).zfill(len(str(len(numeric_lines)))))+".png") # DPI can be specified, determines resolution of output images
        plt.clf() # Close plots to prevent memory issue

print("Finished outputting images, creating GIF...")

# filepaths
fp_in = dir+"archive_animated/"+title+"*.png" # Specify all generated images
fp_out = dir+"archive_animated/"+title+"_archive.gif" # Output file name

img, *imgs = [Image.open(f) for f in sorted(glob.glob(fp_in))]
img.save(fp=fp_out, format='GIF', append_images=imgs,
         save_all=True, duration=200, loop=0) # Save gif from images
     
print("All done!")