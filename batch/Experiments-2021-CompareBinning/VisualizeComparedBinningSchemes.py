"""
python VisualizeComparedBinningSchemes.py mariocomparebins DistinctNSDecorate
"""
import sys
import os
import numpy
import matplotlib.pyplot as plt

def file_contents_to_dictionary(original_scheme, data_array):
    data_dict = {}
    data_dict["original_scheme"] = original_scheme
    data_dict["name"] = data_array[0][(data_array[0].rfind("_comparedTo_")+12):data_array[0].rfind("_log.txt")]
    data_dict["occupied_bins"] = int(data_array[1].replace("Occupied Bins: ", ""))
    data_dict["overall_bins"] = int(data_array[2][(data_array[2].rfind("/")+1):(len(data_array[2])-1)]) # Occupied num
    data_dict["original_bins"] = int(data_array[3][(data_array[3].rfind("/")+1):(len(data_array[3])-1)]) # Surviving num
    data_dict["qd"] = float(data_array[4].replace("QD Score: ", ""))
    data_dict["max_fitness"] = float(data_array[5].replace("Maximum Fitness: ", ""))
    return data_dict


def data_mean_error(input_data):
    organized_data = {}
    organized_data["val"] = numpy.mean(input_data)
    organized_data["standard_deviation"] = numpy.std(input_data)
    return organized_data


def convert_data_to_averages(data_array):
    processed = {}
    processed["occupied_bins"] = data_mean_error([v["occupied_bins"] for v in data_array])
    processed["overall_bins"] = data_mean_error([((v["occupied_bins"]/v["overall_bins"])*100) for v in data_array])
    processed["original_bins"] = data_mean_error([((v["occupied_bins"]/v["original_bins"])*100) for v in data_array])
    processed["qd"] = data_mean_error([v["qd"] for v in data_array])
    processed["max_fitness"] = data_mean_error([v["max_fitness"] for v in data_array])
    return processed
    

try: # Get the file path from arguments
    folder_path = sys.argv[1]
    print("Folder input: " + folder_path)
except:
    print("Folder should be specified as first argument!")
    quit()
    
try: # Get the file path from arguments
    base_bins = sys.argv[2]
    print("Bin type input: " + base_bins)
except:
    base_bins = ""
    print("Bins type not specified, targetting all types in \""+folder_path+"\"")
    


directories = os.listdir(folder_path)
targetted_directories = []
for dir in directories:
    if base_bins in dir and "." not in dir and "Line" not in dir: # Remove "Line" from directories since results are not important
        targetted_directories.append(dir)

targetted_directories.sort()


converted_data = []
for target in targetted_directories:
    files = os.listdir(folder_path + "/" + target)
    valid_files = []
    for fil in files:
        if "_comparedTo_" in fil and "_MAPElites_" not in fil:
            valid_files.append(fil)
    
    file_data = []
    for valid in valid_files:
        temp_data = [valid]
        abs_path = os.path.abspath(folder_path + "/" + target + "/" + valid).replace("\\", "\\\\")
        exec("abs_path = u\"\\\\\\\\?\\\\"+abs_path+"\"")
        with open(abs_path, "r") as opened_file:
            for line in opened_file: # iterate through lines
                temp_data.append(line.strip())
            file_data.append(temp_data)
    
    for i in file_data:
        converted_data.append(file_contents_to_dictionary(target, i))

# Sort data by the type of the original binning scheme
segmented_data = {}
for i in range(len(converted_data)):
    if converted_data[i]["original_scheme"][:-1] not in segmented_data:
        segmented_data[converted_data[i]["original_scheme"][:-1]] = []
    segmented_data[converted_data[i]["original_scheme"][:-1]].append(converted_data[i])

# Sort the data by the type of bin that is being compared to
scheme_sorted_data = {}
for scheme in segmented_data:
    sorted_scheme = sorted(segmented_data[scheme], key=lambda k: k["name"]) 
    scheme_sorted_data[scheme] = sorted_scheme

# Separate things into arrays
final_sorted_data = {}
for i in scheme_sorted_data:
    final_sorted_data[i] = {}
    for j in scheme_sorted_data[i]:
        #print(j)
        if j["name"] not in final_sorted_data[i]:
            final_sorted_data[i][j["name"]] = []
        final_sorted_data[i][j["name"]].append(j)

# Process data into averages with standard deviations
averaged_data = {}
for i in final_sorted_data:
    averaged_data[i] = {}
    for j in final_sorted_data[i]:
        averaged_data[i][j] = convert_data_to_averages(final_sorted_data[i][j])


for i in averaged_data:
    titles = []
    vals = []
    errs = []
    colors = []
    for j in averaged_data[i]:
        current = averaged_data[i][j]
        left_label_bound = j.rfind("MAPElites")+9
        if left_label_bound == 8:
            titles.append(j[:j.rfind("BinLabels")])
        else:
            titles.append(j[left_label_bound:j.rfind("BinLabels")]) # Cut out "MAPElites" part of 
        vals.append(current["original_bins"]["val"])
        errs.append(current["original_bins"]["standard_deviation"])
        colors.append("red")
    fig, ax = plt.subplots()
    ax.bar(
        titles, 
        vals, 
        yerr=errs,
        color=colors
        )
    plt.ylim(0, 100) 
    plt.xticks(rotation=25)
    plt.tight_layout()
    ax.set(title=(folder_path[:folder_path.rfind("comparebins")].title()+" "+i))
    plt.savefig(folder_path+"/"+i+".png") # Save file
    #plt.show() # Show the plot
