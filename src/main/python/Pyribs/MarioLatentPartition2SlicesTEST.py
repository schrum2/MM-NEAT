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
def sum_halves(sol):
    """
        Calculates the behavior characterization by summing like normal
    """
    dim = sol.shape[1] # sol typically has more than one solution, so checks second dim of solution 2D array

    # Calculate BCs.
    copied = sol.copy()
    bcs = np.concatenate( # numpy functions work on 2D arrays, this is just to keep parity with summing despite the fact we only have 1 in the "batch"
        (
            np.sum(copied[:, :dim // 2], axis=1, keepdims=True),
            np.sum(copied[:, dim // 2:], axis=1, keepdims=True),
        ),
        axis=1,
    )
    return bcs


def create_optimizer(algorithm, dim, seed):
    """Creates an optimizer based on the algorithm name. (THIS TEXT FRM PYRIBS)
    
    Args:
        algorithm (str): Name of the algorithm passed into sphere_main.
        dim (int): Dimensionality of the sphere function.
        seed (int): Main seed or the various components.
    Returns:
        Optimizer: A ribs Optimizer for running the algorithm.
    """
    max_bound = dim / 2 # max bound for dim size of 10 is -5 to 5 in each dimension
    bounds = [(-max_bound, max_bound), (-max_bound, max_bound)]
    initial_sol = np.zeros(dim) # Inital solution of zeros
    
    emitter_bounds = [] 
    for i in range(dim):
        emitter_bounds.append((-1, 1)) # Bound each value in solution between -1 and 1
    batch_size = 1 # Since we do one at a time there is only 1 in a "batch"
    num_emitters = 1 
    # https://docs.pyribs.org/en/stable/api/ribs.archives.GridArchive.html#ribs.archives.GridArchive
    archive = GridArchive((100, 100), bounds, seed=seed)

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
def get_data_from_level(string_level):
    jar.stdin.write(str(string_level[0].tolist())+ "\n") # Send vector to jar
    jar.stdin.flush()
    coords = ""
    while "[" not in coords:
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
    data_dict["stats"] = [data_dict["stats[0]"], data_dict["stats[1]"]] # Put stats into array instead of separated
    del data_dict["stats[0]"]
    del data_dict["stats[1]"]
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
    algorithm = "map_elites" # Algorithm
    dim=10 # Length of solution vector to be expected
    iterations = 5000 # Total number of iterations
    outdir=f"mariolatentpartition2slices_pyribs_{algorithm}" # Output directory
    log_freq=100 # Logging frequency
    max_fitness = 120 # depends on number of segments (level chunks)
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
            data_out = get_data_from_level(sols) # Get score (and other data) from jar
            bcs = sum_halves(sols) # Get bcs
            """
                optimizer.tell() takes in two array-likes, one for scores 
                and one for the behavior characterizations. Since out batch
                size is only one, this function must be passed a list that
                only contains a single value, either a score or a bc based
                on which it is. I messed up the lode runner one because I
                hadn't embedded either within another array so just make sure!
            """
            optimizer.tell([data_out["binScore"]], bcs) # Send optimizer (and archive) array of scores and bcs, however batch size of one means only one of each
            non_logging_time += time.time() - itr_start
            progress() # Progress bar stuff

            # Logging and output.
            final_itr = itr == iterations
            if itr % log_freq == 0 or final_itr: # Stuff to do on logging frequency steps
                data = archive.as_pandas(include_solutions=final_itr)
                if final_itr:
                    data.to_csv(str(outdir / f"{name}_archive.csv"))

                # Record and display metrics.
                total_cells = 500 * 500
                metrics["QD Score"]["x"].append(itr)
                metrics["QD Score"]["y"].append(data['objective'].sum())
                metrics["Archive Coverage"]["x"].append(itr)
                metrics["Archive Coverage"]["y"].append(len(data))
                print(f"Iteration {itr}\t| Archive Coverage: "
                      f"{metrics['Archive Coverage']['y'][-1]/100:.3f}% "
                      f"QD Score: {metrics['QD Score']['y'][-1]:.3f}") # Console output
                
                save_heatmap(archive, str(outdir / f"{name}_heatmap_{itr:05d}.png"), [0, 400])


if __name__ == '__main__':
    ### INITALIZE GAN STUFF

    # Start running JAR
    jar = Popen(["ExternalMario-LatentPartition2Slices.bat", "0"], encoding='ascii', stdin=PIPE, stdout=PIPE)
    # Seek to end of JAR
    s = ""
    while s != "READY":
        s = jar.stdout.readline().strip()
        #print("<From JAR> " + s) # DEBUG
        
    fire.Fire(pyribs_main) # Run with progress bar
    
    # Exit JAR and close stdout
    jar.stdin.write("exit")
    jar.stdout.close()


