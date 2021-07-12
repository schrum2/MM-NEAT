import torch
import torchvision.utils as vutils
from torch.autograd import Variable

import sys
import json
import time
import numpy as np
import models.dcgan as dcgan
import models.cdcgan as cdcgan
import math

import random
from collections import OrderedDict

from subprocess import Popen, PIPE, STDOUT

from pathlib import Path

import fire
import matplotlib.pyplot as plt
from matplotlib import colors, cm
from alive_progress import alive_bar

from ribs.archives import GridArchive
from ribs.emitters import (ImprovementEmitter, GaussianEmitter)
from ribs.optimizers import Optimizer
from ribs.visualize import grid_archive_heatmap

### FUNCTIONS

# Get behavior characterization from dictionary associated with a single solution
def behavior_characterization(data_out):
    # For now, assume Distinct ASAD in Mario
    
    distinct = float(data_out["Distinct Segments"])
    #alternating = np.zeros(3)
    #index = 1
    #while index < len(data_out["stats"]):
    #    stats0 = np.array(data_out["stats"][index-1]) 
    #    stats1 = np.array(data_out["stats"][index])
    #    alternating = alternating + abs(stats0 - stats1)
    
    # Magic numbers
    #altDecoration = alternating[0]
    #altSpace = alternating[2]
        
    #print("Compare Python {} to Java {}".format([distinct, altSpace, altDecoration],data_out['Bin Coordinates'])
    
    # Can't use 3D. Map to 2D
    #return [distinct, altSpace, altDecoration]
    
    coords = data_out['Bin Coordinates']
    return [((distinct - 1)%5)*10 + coords[2], ((distinct - 1)%2)*10 + coords[1]]

def create_optimizer(algorithm, dim, seed):
    """Creates an optimizer based on the algorithm name. (THIS TEXT FROM PYRIBS)
    
    Args:
        algorithm (str): Name of the algorithm passed into sphere_main.
        dim (int): Dimensionality of the sphere function.
        seed (int): Main seed or the various components.
    Returns:
        Optimizer: A ribs Optimizer for running the algorithm.
    """
    
    # Project the 3D archive into a 2D archive.
    # A 2 by 5 grid of 10 by 10 cells
    bounds = [(0, 50), (0, 20)]
    initial_sol = np.zeros(dim) # Inital solution of zeros
    
    emitter_bounds = [] 
    for i in range(dim):
        emitter_bounds.append((-1, 1)) # Bound each value in solution between -1 and 1
    batch_size = 37 # Since we do one at a time there is only 1 in a "batch"
    num_emitters = 5 
    # https://docs.pyribs.org/en/stable/api/ribs.archives.GridArchive.html#ribs.archives.GridArchive
    # Once again, mapping the 3D archive to 2D for display purposes
    archive = GridArchive((50, 20), bounds, seed=seed)

    # Create emitters. Each emitter needs a different seed, so that they do not all do the same thing.
    emitter_seeds = [None] * num_emitters if seed is None else list(range(seed, seed + num_emitters))
    if algorithm in ["map_elites"]:
        emitters = [
            GaussianEmitter(archive,
                            initial_sol,
                            0.5,
                            bounds=emitter_bounds,
                            batch_size=batch_size,
                            seed=s) for s in emitter_seeds
        ]
    elif algorithm in ["line_map_elites"]:
        emitters = [
            IsoLineEmitter(archive,
                           initial_sol,
                           iso_sigma=0.1,
                           line_sigma=0.2,
                           batch_size=batch_size,
                           seed=s) for s in emitter_seeds
        ]
    elif algorithm in ["cma_me_imp"]:
        selection_rule = "filter"
        emitters = [
            ImprovementEmitter(archive,
                               initial_sol,
                               0.5,
                               batch_size=batch_size,
                               selection_rule=selection_rule,
                               seed=s) for s in emitter_seeds
        ]

    return Optimizer(archive, emitters)


# Using the JAR, get the bins and evaluated score from a provided level
def get_data_from_level(one_sol):
    jar.stdin.write(str(one_sol.tolist())+ "\n") # Send vector to jar
    jar.stdin.flush()
    coords = ""
    while "[" not in coords: # Some domains produce additional garbage output. Wait for list of archive indices
        coords = jar.stdout.readline().strip()
    data_dict = {}
    exec("data_dict[\"Bin Coordinates\"] = "+coords) # Bin coords are first, and do not have a "____ = " prefix
    s = jar.stdout.readline().strip()
    while s != "MAP DONE": # Run until MAP DONE
        #print("<From JAR> " + s) # DEBUG
        try:
            exec("data_dict[\""+s.split(" = ")[0]+"\"] = "+s.split(" = ")[1]) # Attempt to parse as given, either int, float, or array
        except:
            data_dict[s.split(" = ")[0]] = s.split(" = ")[1] # If unable, just send as string
        s = jar.stdout.readline().strip() # Read next line
    data_dict["stats"] = []
    index = 0
    while f"stats[{index}]" in data_dict:
        data_dict["stats"].append(data_dict[f"stats[{index}]"])
        del data_dict[f"stats[{index}]"]
        index += 1    

    #print(one_sol, "->", data_dict["binScore"], data_dict["Bin Coordinates"])

    return data_dict


def save_heatmap(archive, heatmap_path, min_max):
    """Saves a heatmap of the archive to the given path.

    Args:
        archive (GridArchive): The archive to save.
        heatmap_path: Image path for the heatmap.
    """
    plt.figure(figsize=(8, 6))
    grid_archive_heatmap(archive, vmin=min_max[0], vmax=min_max[1]) # This is a pyribs thing it just works
    plt.tight_layout()
    plt.savefig(heatmap_path)


### MAIN

def pyribs_main():
    algorithm = "cma_me_imp" # "map_elites" # Algorithm
    dim=50 # 10 segments with 5 latent variables each
    iterations = 100000 # Total number of iterations (change because of batch size?)
    outdir=f"marioDistinctASAD_pyribs_{algorithm}" # Output directory
    log_freq=100 # Logging frequency
    max_fitness = 500 # depends on number of segments (level chunks)
    name = f"{algorithm}_{dim}" # Name for output images and data
    
    outdir = Path(outdir)
    if not outdir.is_dir():
        outdir.mkdir()
    
    optimizer = create_optimizer(algorithm, dim, None) # Make optimizer with contained archive
    archive = optimizer.archive # Get archive
    metrics = { # Set up starter metrics, will be populated later
        "QD Score": {
            "x": [0],
            "y": [0.0],
        },
        "Archive Coverage": {
            "x": [0],
            "y": [0.0],
        },
    }
    
    non_logging_time = 0.0
    with alive_bar(iterations) as progress:
        save_heatmap(archive, str(outdir / f"{name}_heatmap_{0:05d}.png"), [0, max_fitness])

        for itr in range(1, iterations + 1):
            itr_start = time.time()
            sols = optimizer.ask()
            scores = []
            bcs = []
            for s in sols:
                data_out = get_data_from_level(s) # Get score (and other data) from jar
                scores.append(data_out["binScore"])
                bcs.append(behavior_characterization(data_out))
            
            optimizer.tell(scores, bcs) # Send optimizer (and archive) array of scores and bcs
            non_logging_time += time.time() - itr_start
            progress() # Progress bar stuff

            # Logging and output.
            final_itr = itr == iterations
            if itr % log_freq == 0 or final_itr: # Stuff to do on logging frequency steps
                data = archive.as_pandas(include_solutions=final_itr)
                if final_itr:
                    data.to_csv(str(outdir / f"{name}_archive.csv"))

                # Record and display metrics.
                total_cells = 10 * 10 * 10
                metrics["QD Score"]["x"].append(itr)
                metrics["QD Score"]["y"].append(data['objective'].sum())
                metrics["Archive Coverage"]["x"].append(itr)
                metrics["Archive Coverage"]["y"].append(len(data))
                print(f"Iteration {itr}\t| Archive Coverage: "
                      f"{metrics['Archive Coverage']['y'][-1]/100:.3f}% "
                      f"QD Score: {metrics['QD Score']['y'][-1]:.3f}") # Console output
                
                save_heatmap(archive, str(outdir / f"{name}_heatmap_{itr:05d}.png"), [0, max_fitness])

    # Plot metrics.
    print(f"Algorithm Time (Excludes Logging and Setup): {non_logging_time}s")
    for metric in metrics:
        plt.plot(metrics[metric]["x"], metrics[metric]["y"])
        plt.title(metric)
        plt.xlabel("Iteration")
        plt.savefig(
            str(outdir / f"{name}_{metric.lower().replace(' ', '_')}.png"))
        plt.clf()
    with (outdir / f"{name}_metrics.json").open("w") as file:
        json.dump(metrics, file, indent=2)

if __name__ == '__main__':
    ### INITALIZE GAN STUFF

    # Start running JAR
    jar = Popen(["ExternalMario-DistinctNSDecorate.bat", "0"], encoding='ascii', stdin=PIPE, stdout=PIPE)
    # Seek to end of JAR
    s = ""
    while s != "READY":
        s = jar.stdout.readline().strip()
        #print("<From JAR> " + s) # DEBUG
        
    fire.Fire(pyribs_main) # Run with progress bar
    
    # Exit JAR and close stdout
    jar.stdin.write("exit")
    jar.stdout.close()


