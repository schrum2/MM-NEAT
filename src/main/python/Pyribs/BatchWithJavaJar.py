import sys
import json
import time
import numpy as np
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
    global batch_file
    
    if batch_file == "ExternalMario-DistinctNSDecorate.bat":
        # Distinct ASAD in Mario
        distinct = float(data_out["Distinct Segments"])
        coords = data_out['Bin Coordinates']
        altSpaceIndex = coords[1]
        altDecIndex = coords[2]
    
        return [((distinct - 1)%5)*10 + altDecIndex, (1 - int((distinct - 1)/5))*10 + altSpaceIndex]
    elif batch_file == "ExternalMario-LatentPartition2Slices.bat":
        coords = data_out['Bin Coordinates']
        offset = 250
        return [coords[0] - offset, coords[1] - offset]
    elif batch_file == "ExternalLodeRunner-PercentGroundNumGoldAndEnemies.bat":
        coords = data_out['Bin Coordinates'] # groundIndex, treasureIndex, enemyIndex
        groundIndex = coords[0]
        treasureIndex = coords[1]
        enemyIndex = coords[2]
        
        return [enemyIndex, (9 - groundIndex)*10 + treasureIndex]
    else:
        raise ValueError(f"Batch file does not define recognized binning scheme: {batch_file}")

def create_optimizer(algorithm, dim, seed):
    """Creates an optimizer based on the algorithm name. (THIS TEXT FROM PYRIBS)
    
    Args:
        algorithm (str): Name of the algorithm passed into sphere_main.
        dim (int): Dimensionality of the sphere function.
        seed (int): Main seed or the various components.
    Returns:
        Optimizer: A ribs Optimizer for running the algorithm.
    """
    global batch_file
    
    if batch_file == "ExternalMario-DistinctNSDecorate.bat":
        # Project the 3D archive into a 2D archive.
        # A 2 by 5 grid of 10 by 10 cells
        bounds = [(0, 50), (0, 20)]
        archive_size = (50, 20)
    elif batch_file == "ExternalMario-LatentPartition2Slices.bat":
        bounds = [(-250, 250), (-250, 250)]
        archive_size = (500, 500)
    elif batch_file == "ExternalLodeRunner-PercentGroundNumGoldAndEnemies.bat":
        # Project the 3D archive into a 2D archive.
        # A 1 by 10 grid of 10 by 10 cells (vertical)
        bounds = [(0, 10), (0, 100)]
        archive_size = (10, 100)
    else:
        raise ValueError(f"Batch file does not define recognized binning scheme: {batch_file}")
        
    initial_sol = np.zeros(dim) # Inital solution of zeros
    
    emitter_bounds = [] 
    for i in range(dim):
        emitter_bounds.append((-1, 1)) # Bound each value in solution between -1 and 1
    batch_size = 37 # Since we do one at a time there is only 1 in a "batch"
    num_emitters = 5 
    # https://docs.pyribs.org/en/stable/api/ribs.archives.GridArchive.html#ribs.archives.GridArchive
    # Once again, mapping the 3D archive to 2D for display purposes
    archive = GridArchive(archive_size, bounds, seed=seed)

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
def get_data_from_solution(one_sol):
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
    global batch_file
    global dim
    
    if batch_file == "ExternalMario-DistinctNSDecorate.bat":
        dim=50 # 10 segments with 5 latent variables each
        # For comparison, I want to evaluate 100000 individuals.
        # With 5 emitters and a batch size of 37, 185 are evaluated per iteration.
        # 100000 / 185 is 540.5405405405405, so run for 541 iterations
        iterations = 541
        outdir=f"marioDistinctASAD_pyribs_{algorithm}_{run_num}" # Output directory
        log_freq=50 # Logging frequency
        max_fitness = 500 # depends on number of segments (level chunks)
        total_cells = 10 * 10 * 10
    elif batch_file == "ExternalMario-LatentPartition2Slices.bat":
        dim=10 # Length of solution vector to be expected
        iterations = 20000 # Total number of iterations
        outdir=f"marioLatentPartition2Slices_pyribs_{algorithm}_{run_num}" # Output directory
        log_freq=100 # Logging frequency
        max_fitness = 120 # depends on number of segments (level chunks)
        total_cells = 500 * 500
    elif batch_file == "ExternalLodeRunner-PercentGroundNumGoldAndEnemies.bat":
        dim=10 
        # For comparison, I want to evaluate 50000 individuals.
        # With 5 emitters and a batch size of 37, 185 are evaluated per iteration.
        # 50000 / 185 is 270.27027027, so run for 271 iterations
        iterations = 271
        outdir=f"lodeRunnerGroundGoldEnemies_pyribs_{algorithm}_{run_num}" # Output directory
        log_freq=50 # Logging frequency
        max_fitness = 650 
        total_cells = 10 * 10 * 10
    else:
        raise ValueError(f"Batch file does not define recognized binning scheme: {batch_file}")
    
    
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
                data_out = get_data_from_solution(s) # Get score (and other data) from jar
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
                metrics["QD Score"]["x"].append(itr)
                metrics["QD Score"]["y"].append(data['objective'].sum())
                metrics["Archive Coverage"]["x"].append(itr)
                metrics["Archive Coverage"]["y"].append(len(data))
                print(f"Iteration {itr}\t| Archive Coverage: "
                      f"{metrics['Archive Coverage']['y'][-1]/total_cells:.3f}% "
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
    global batch_file
    global algorithm
    global run_num
    ### INITALIZE GAN STUFF
    batch_file = sys.argv[1] # A batch file that launches evals for a specific domain with specific binning scheme, like ExternalMario-DistinctNSDecorate.bat
    run_num = sys.argv[2]
    algorithm = sys.argv[3] # "map_elites" or "cma_me_imp"
    if algorithm not in ["map_elites", "cma_me_imp"]:
        print(f"Algorithm is not known: {algorithm}")
        print("Use one of", ["map_elites", "cma_me_imp"])
        quit()

    # Start running JAR
    jar = Popen([batch_file, run_num], encoding='ascii', stdin=PIPE, stdout=PIPE)
    # Seek to end of JAR
    s = ""
    while s != "READY":
        s = jar.stdout.readline().strip()
        #print("<From JAR> " + s) # DEBUG
        
    fire.Fire(pyribs_main) # Run with progress bar
    
    # Exit JAR and close stdout
    jar.stdin.write("exit")
    jar.stdout.close()


