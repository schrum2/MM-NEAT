"""
python VisualizeComparedBinningSchemes.py mariocomparebins DistinctNSDecorate
"""
import sys
import os

def file_contents_to_dictionary(data_array):
    data_dict = {}
    data_dict["name"] = data_array[0][(data_array[0].rfind("_comparedTo_")+12):data_array[0].rfind("_log.txt")]
    data_dict["occupied_bins"] = int(data_array[1].replace("Occupied Bins: ", ""))
    data_dict["overall_bins"] = int(data_array[2][(data_array[2].rfind("/")+1):(len(data_array[2])-1)]) # Occupied num
    data_dict["original_bins"] = int(data_array[3][(data_array[3].rfind("/")+1):(len(data_array[2])-1)]) # Surviving num
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
    print("Bins should be specified as second argument!")
    quit()
    
directories = os.listdir(folder_path)
targetted_directories = []
for dir in directories:
    if base_bins in dir:
        targetted_directories.append(dir)

targetted_directories.sort()

#print(targetted_directories)

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
        opened_file = open(folder_path + "/" + target + "/" + valid, "r")
        for line in opened_file: # iterate through lines
            temp_data.append(line.strip())
        file_data.append(temp_data)
        
    #for i in file_data:
    #    for j in i:
    #        print(j)
    print(file_contents_to_dictionary(file_data[1]))
    
    quit()