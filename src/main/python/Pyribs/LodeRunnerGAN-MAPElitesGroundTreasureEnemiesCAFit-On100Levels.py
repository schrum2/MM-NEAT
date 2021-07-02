
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
def create_optimizer(algorithm, dim, seed):
    """Creates an optimizer based on the algorithm name.

    Args:
        algorithm (str): Name of the algorithm passed into sphere_main.
        dim (int): Dimensionality of the sphere function.
        seed (int): Main seed or the various components.
    Returns:
        Optimizer: A ribs Optimizer for running the algorithm.
    """
    max_bound = dim*10
    # Ground %, Gold, Enemies
    bounds = [(0, dim*1), (0, dim*50), (0, dim*20)]
    initial_sol = np.zeros(dim)
    batch_size = 1
    num_emitters = 1

    archive = GridArchive((10, 10, 10), bounds, seed=seed)

    # Create emitters. Each emitter needs a different seed, so that they do not all do the same thing.
    emitter_seeds = [None] * num_emitters if seed is None else list(range(seed, seed + num_emitters))
    if algorithm in ["map_elites"]:
        emitters = [
            GaussianEmitter(archive,
                            initial_sol,
                            0.5,
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

# Using the GAN, processes a latent vector (as a numpy array) into a string representation of a level.
def get_level_from_latent_vector(latent_vector_array):
    latent_vector = torch.FloatTensor( latent_vector_array ).view(1, nz, 1, 1) 
    levels = generator(Variable(latent_vector, volatile=True))
    level = levels.data.cpu().numpy()
    level = level[:,:,:22,:32] # HEIGHT 22, WIDTH 32
    level = np.argmax( level, axis = 1)
    return json.dumps(level[0].tolist())


# Using the JAR, get the bins and evaluated score from a provided level
def get_data_from_level(string_level):
    jar.stdin.write((string_level+"\n"))
    jar.stdin.flush()
    coords = ""
    while "[" not in coords:
        coords = jar.stdout.readline().strip()
    data_dict = {}
    exec("data_dict[\"Bin Coordinates\"] = "+coords)
    s = jar.stdout.readline().strip()
    while s != "MAP DONE":
        #print("<From JAR> " + s)
        data_dict[s.split(" = ")[0]] = float(s.split(" = ")[1])
        s = jar.stdout.readline().strip()
    return data_dict


def save_heatmap(archive, heatmap_path, min_max):
    """Saves a heatmap of the archive to the given path.

    Args:
        archive (GridArchive or CVTArchive): The archive to save.
        heatmap_path: Image path for the heatmap.
    """

    norm = colors.Normalize(vmin=min_max[0], vmax=min_max[1]) # normalize colors
    
    archive_slice_arrays = archive._objective_values

    cmap = "viridis" # Colormap to use

    dimensions = [10, 10, 10]
    columns = 5
    rows = 2

    fig, axs = plt.subplots(nrows=rows, ncols=columns, constrained_layout=True, figsize=(columns*4, rows*3)) # Make subplots

    fig.colorbar(cm.ScalarMappable(norm=norm, cmap=cmap), ax=axs[:, :], location='right', aspect=50) # Make colorbar

    fig.suptitle("TITLE")

    counter = 0
    for ax, slice in zip(axs.flat, archive_slice_arrays):
        ax.imshow(slice, extent=[0, dimensions[2], dimensions[1], 0], norm=norm, cmap=cmap)
        ax.set_ylim(bottom=0.0, top=dimensions[1])
        ax.set_xlim(left=0.0, right=dimensions[2])
        #ax.set_xlabel(dimension_names[2]) # Add labels
        #ax.set_ylabel(dimension_names[1])
        #ax.set_title(dimension_names[0]+": "+str(counter))
        counter+=1

    plt.show()
    plt.savefig(heatmap_path) # Save file


### MAIN

def pyribs_main():
    algorithm = "map_elites"
    dim=10
    iterations = 500
    outdir="loderunner_output"
    log_freq=20
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
        #save_heatmap(archive, str(outdir / f"{name}_heatmap_{0:05d}.png"), -((-5.12 - (5.12 * 0.4))**2 * dim))
        save_heatmap(archive, str(outdir / f"{name}_heatmap_{0:05d}.png"), [0, 500])

        for itr in range(1, iterations + 1):
            itr_start = time.time()
            sols = optimizer.ask()
            #print(sols)
            data_out = get_data_from_level(get_level_from_latent_vector(sols))
            objs = [data_out["Ground Percent"], data_out["Treasures"], data_out["Enemies"]]
            bcs = [data_out["Ground Percent"], data_out["Treasures"], data_out["Enemies"]]
            optimizer.tell(objs, bcs)
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
                print(f"Iteration {itr} | Archive Coverage: "
                      f"{metrics['Archive Coverage']['y'][-1]:.3f}% "
                      f"QD Score: {metrics['QD Score']['y'][-1]:.3f}")
                
                save_heatmap(archive, str(outdir / f"{name}_heatmap_{itr:05d}.png"), [0, 500])
    

"""
while True:
    arr = np.array(json.loads("[0,0,0,0,0,0,0,0,0,0]"))
    data_out = get_data_from_level(get_level_from_latent_vector(arr))
    print(data_out)
    input()
"""

if __name__ == '__main__':
    ### INITALIZE GAN STUFF
    # Simulates these parameters   ->   .\src\main\python\GAN\LodeRunnerGAN\LodeRunnerAllGround20LevelsEpoch20000_10_7.pth 10 7 32 22

    nz = 10
    generator = dcgan.DCGAN_G(32, nz, 7, 64, 1, 0) # imageSize, nz, z_dims, ngf, ngpu, n_extra_layers
    deprecatedModel = torch.load(".\src\main\python\GAN\LodeRunnerGAN\LodeRunnerAllGround20LevelsEpoch20000_10_7.pth", map_location=lambda storage, loc: storage)
    fixedModel = OrderedDict()
    for (goodKey,ignore) in generator.state_dict().items():
        badKey = goodKey.replace(":",".")
        if badKey in deprecatedModel:
            goodValue = deprecatedModel[badKey]
            fixedModel[goodKey] = goodValue
    generator.load_state_dict(deprecatedModel)

    # Start running JAR
    jar = Popen(["java", "-jar", "LodeRunnerGAN-MAPElitesGroundTreasureEnemiesCAFit-On100Levels-HashMap.jar"], encoding='ascii', stdin=PIPE, stdout=PIPE)
    # Seek to end of JAR
    s = ""
    while s != "READY":
        s = jar.stdout.readline().strip()
        #print("<From JAR> " + s)
        
    fire.Fire(pyribs_main)
    
    # Exit JAR and close stdout
    jar.stdin.write("exit")
    jar.stdout.close()


