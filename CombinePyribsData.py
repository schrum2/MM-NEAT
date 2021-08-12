import numpy as np
import matplotlib.pyplot as plt
import sys
import json

if __name__ == "__main__":
    jsons = sys.argv[1:]
    print(f"Combine {jsons}")
    
    for f in jsons:
        with open(f) as jsonFile:
            jsonContents = json.load(jsonFile)
            x = jsonContents["QD Score"]["x"]
            y = jsonContents["QD Score"]["y"]
            if "map_elites" in f: algo = "MAP-Elites"
            elif "cma_me_imp" in f: algo = "CMA-ME (Improvement)"
            plt.plot(x,y, label = algo)
            
    plt.title("QD Score")
    plt.legend()
    plt.savefig("qd.png")
    plt.show()
    
    for f in jsons:
        with open(f) as jsonFile:
            jsonContents = json.load(jsonFile)
            x = jsonContents["Archive Coverage"]["x"]
            y = jsonContents["Archive Coverage"]["y"]
            if "map_elites" in f: algo = "MAP-Elites"
            elif "cma_me_imp" in f: algo = "CMA-ME (Improvement)"
            plt.plot(x,y, label = algo)
            
    plt.title("Archive Coverage")
    plt.legend()
    plt.savefig("coverage.png")
    plt.show()
