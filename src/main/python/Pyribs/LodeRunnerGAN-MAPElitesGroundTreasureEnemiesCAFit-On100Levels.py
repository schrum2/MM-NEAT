"""
from subprocess import Popen, PIPE, STDOUT

jar = Popen(["java", "-jar", "LodeRunnerGAN-MAPElitesGroundTreasureEnemiesCAFit-On100Levels.jar"], encoding='ascii', stdin=PIPE, stdout=PIPE)

s = "" # Output string
while s != "READY":
    s = jar.stdout.readline().strip()
    print("<From JAR> " + s) #

jar.stdin.write('[[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4], [0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 4], [0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4], [2, 0, 2, 0, 0, 2, 0, 2, 2, 2, 2, 0, 0, 2, 0, 2, 2, 2, 2, 0, 0, 2, 0, 2, 2, 2, 2, 2, 2, 2, 0, 4], [0, 0, 2, 0, 0, 2, 2, 2, 2, 0, 2, 2, 2, 0, 2, 2, 2, 0, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4], [2, 0, 3, 2, 3, 2, 2, 3, 2, 2, 2, 2, 0, 0, 0, 3, 2, 2, 2, 2, 0, 0, 0, 3, 2, 3, 3, 3, 3, 3, 3, 4], [4, 0, 2, 0, 2, 0, 0, 2, 4, 2, 0, 0, 0, 0, 0, 2, 4, 2, 0, 0, 0, 0, 0, 2, 4, 0, 0, 6, 2, 3, 3, 4], [4, 0, 0, 0, 0, 0, 3, 3, 4, 6, 0, 0, 0, 0, 0, 3, 4, 6, 0, 0, 0, 0, 0, 3, 4, 3, 3, 3, 3, 3, 0, 4], [4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0], [0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3], [2, 0, 2, 0, 0, 2, 0, 2, 2, 2, 2, 0, 0, 2, 0, 2, 2, 2, 2, 0, 0, 2, 0, 2, 2, 2, 2, 2, 2, 0, 0, 2], [0, 0, 2, 0, 0, 2, 2, 2, 2, 0, 2, 2, 2, 0, 2, 2, 2, 0, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4], [2, 0, 3, 2, 3, 2, 2, 3, 2, 2, 2, 2, 0, 0, 0, 3, 2, 2, 2, 2, 0, 0, 0, 3, 2, 3, 3, 3, 3, 3, 3, 4], [4, 0, 2, 0, 2, 0, 0, 2, 4, 2, 0, 0, 0, 0, 0, 2, 4, 2, 0, 0, 0, 0, 0, 2, 4, 0, 0, 6, 2, 3, 3, 4], [4, 0, 0, 0, 0, 0, 3, 3, 4, 6, 0, 0, 0, 0, 0, 3, 4, 6, 0, 0, 0, 0, 0, 3, 4, 3, 3, 3, 3, 3, 0, 4], [4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0], [0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3]]\n')
print("Message sent")
jar.stdin.flush()
print("<From JAR> " + jar.stdout.readline().strip())
print("<From JAR> " + jar.stdout.readline().strip())
print("Message recieved")

# Exit JAR and close stdout
jar.stdin.write("exit")
jar.stdout.close()
"""

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
from alive_progress import alive_bar

from ribs.archives import CVTArchive, GridArchive
from ribs.emitters import (GaussianEmitter, ImprovementEmitter, IsoLineEmitter,
                           OptimizingEmitter, RandomDirectionEmitter)
from ribs.optimizers import Optimizer
from ribs.visualize import cvt_archive_heatmap, grid_archive_heatmap

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
    bounds = [(0, max_bound), (0, max_bound), (0, max_bound)]
    initial_sol = np.zeros(dim)
    batch_size = 37
    num_emitters = 15

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
    coords = jar.stdout.readline().strip()
    data_dict = {}
    exec("data_dict[\"Bin Coordinates\"] = "+coords)
    s = jar.stdout.readline().strip()
    while s != "MAP DONE":
        #print("<From JAR> " + s)
        data_dict[s.split(" = ")[0]] = float(s.split(" = ")[1])
        s = jar.stdout.readline().strip()
    return data_dict


### MAIN

def pyribs_main():
    algorithm = "map_elites"
    dim=10
    itrs=4500,
    outdir="sphere_output",
    log_freq=250
    
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

pyribs_main()

while True:
    arr = np.array(json.loads("[0,0,0,0,0,0,0,0,0,0]"))
    data_out = get_data_from_level(get_level_from_latent_vector(arr))
    print(data_out)
    input()



# Exit JAR and close stdout
jar.stdin.write("exit")
jar.stdout.close()
