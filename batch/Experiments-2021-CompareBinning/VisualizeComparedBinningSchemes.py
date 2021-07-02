"""
python VisualizeComparedBinningSchemes.py mariocomparebins DistinctNSDecorate
"""
import sys
import os

import os.path
from os import path

#test_path = u"\\\\?\\G:\\.shortcut-targets-by-id\\0B0N6MBJZ1slKRFpSRjMyY3luZFE\SCOPE Artifacts\\SCOPE 2021 MAP-Elites Comparison\\mariocomparebins\\LineKLDivergence222and620\\MarioCompareBins-LineKLDivergence222and620_comparedTo_MarioMAPElitesDistinctChunksNSAndLeniencyBinLabels_log.txt"
#print(test_path)
#print(path.exists(test_path))
#quit()

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
    if base_bins in dir and "." not in dir:
        targetted_directories.append(dir)

targetted_directories.sort()

#print(targetted_directories)

converted_data = []
for target in targetted_directories:
    files = os.listdir(folder_path + "/" + target)
    valid_files = []
    for fil in files:
        if "_comparedTo_" in fil and "_MAPElites_" not in fil:
            valid_files.append(fil)
    #print(valid_files)
    
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
        #print(file_contents_to_dictionary(target, i))
        converted_data.append(file_contents_to_dictionary(target, i))
    
    
    #quit()    

segmented_data = {}
for i in range(len(converted_data)):
    #print(converted_data[i]["original_scheme"][:-1])
    if converted_data[i]["original_scheme"][:-1] not in segmented_data:
        segmented_data[converted_data[i]["original_scheme"][:-1]] = []
    segmented_data[converted_data[i]["original_scheme"][:-1]].append(converted_data[i])

for i in segmented_data:
    for j in segmented_data[i]:
        print(j)
    quit()







