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
    dim = sol.shape[1]

    # Normalize the objective to the range [0, 100] where 100 is optimal.
#    best_obj = 0.0
#    worst_obj = (-5.12 - sphere_shift)**2 * dim
    #print("SOLUTION=")
    #print(sol)
#    objs = (raw_obj - worst_obj) / (best_obj - worst_obj) * 100

    # Calculate BCs.
    copied = sol.copy()
    bcs = np.concatenate(
        (
            np.sum(copied[:, :dim // 2], axis=1, keepdims=True),
            np.sum(copied[:, dim // 2:], axis=1, keepdims=True),
        ),
        axis=1,
    )
    #print("BC=")
    #print(bcs)
    return bcs


def create_optimizer(algorithm, dim, seed):
    """Creates an optimizer based on the algorithm name.

    Args:
        algorithm (str): Name of the algorithm passed into sphere_main.
        dim (int): Dimensionality of the sphere function.
        seed (int): Main seed or the various components.
    Returns:
        Optimizer: A ribs Optimizer for running the algorithm.
    """
    max_bound = dim / 2
    bounds = [(-max_bound, max_bound), (-max_bound, max_bound)]
    initial_sol = np.zeros(dim)
    
    emitter_bounds = []
    for i in range(dim):
        emitter_bounds.append((-1, 1))
    batch_size = 1
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
    #print(str(string_level[0].tolist()))
    jar.stdin.write(str(string_level[0].tolist())+ "\n")
    jar.stdin.flush()
    coords = ""
    while "[" not in coords:
        coords = jar.stdout.readline().strip()
    data_dict = {}
    exec("data_dict[\"Bin Coordinates\"] = "+coords)
    s = jar.stdout.readline().strip()
    while s != "MAP DONE":
        #print("<From JAR> " + s)
        try:
            exec("data_dict[\""+s.split(" = ")[0]+"\"] = "+s.split(" = ")[1])
            #data_dict[s.split(" = ")[0]] = float(s.split(" = ")[1])
        except:
            data_dict[s.split(" = ")[0]] = s.split(" = ")[1]
        s = jar.stdout.readline().strip()
    data_dict["stats"] = [data_dict["stats[0]"], data_dict["stats[1]"]]
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
    grid_archive_heatmap(archive, vmin=min_max[0], vmax=min_max[1])
    plt.tight_layout()
    plt.savefig(heatmap_path)


### MAIN

def pyribs_main():
    algorithm = "map_elites"
    dim=10
    iterations = 5000
    outdir="mariolatentpartition2slices_pyribs"
    log_freq=100
    name = f"{algorithm}_{dim}"
    
    outdir = Path(outdir)
    if not outdir.is_dir():
        outdir.mkdir()
    
    optimizer = create_optimizer(algorithm, dim, None)
    archive = optimizer.archive
    metrics = {
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
        save_heatmap(archive, str(outdir / f"{name}_heatmap_{0:05d}.png"), [0, 400])

        for itr in range(1, iterations + 1):
            itr_start = time.time()
            sols = optimizer.ask()
            #print(sols)
            data_out = get_data_from_level(sols)
            #print(data_out)
            bcs = sum_halves(sols)
            #print("binScore= "+str([data_out["binScore"]]), "bc= "+str(bcs))
            optimizer.tell([data_out["binScore"]], bcs)
            non_logging_time += time.time() - itr_start
            progress()

            # Logging and output.
            final_itr = itr == iterations
            if itr % log_freq == 0 or final_itr:
                data = archive.as_pandas(include_solutions=final_itr)
                if final_itr:
                    data.to_csv(str(outdir / f"{name}_archive.csv"))

                # Record and display metrics.
                total_cells = 500 * 500
                metrics["QD Score"]["x"].append(itr)
                metrics["QD Score"]["y"].append(data['objective'].sum())
                metrics["Archive Coverage"]["x"].append(itr)
                metrics["Archive Coverage"]["y"].append(len(data)) #/ total_cells * 100) # Maxx: this can be added to chart percentage filled instead of actual filled
                print(f"Iteration {itr}\t| Archive Coverage: "
                      f"{metrics['Archive Coverage']['y'][-1]/100:.3f}% "
                      f"QD Score: {metrics['QD Score']['y'][-1]:.3f}")
                
                save_heatmap(archive, str(outdir / f"{name}_heatmap_{itr:05d}.png"), [0, 500])


if __name__ == '__main__':
    ### INITALIZE GAN STUFF

    # Start running JAR
    jar = Popen(["ExternalMario-LatentPartition2Slices.bat", "0"], encoding='ascii', stdin=PIPE, stdout=PIPE)
    # Seek to end of JAR
    s = ""
    while s != "READY":
        s = jar.stdout.readline().strip()
        #print("<From JAR> " + s) # DEBUG
        
    fire.Fire(pyribs_main)
    
    # Exit JAR and close stdout
    jar.stdin.write("exit")
    jar.stdout.close()


