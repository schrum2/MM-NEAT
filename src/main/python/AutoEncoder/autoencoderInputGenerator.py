import torch
from torch import nn
from torch.autograd import Variable

import sys
import json
import numpy
from model import autoencoder

# Example (from MM-NEAT): python .\src\main\python\AutoEncoder\autoencoderInputGenerator.py targetimage\skull6\snapshots\iteration30000.pth image
if __name__ == '__main__':
    
    modelToLoad = sys.argv[1] #'sim_autoencoder.pth'
    mode = sys.argv[2] # loss | image
    # loss : code prints MSELoss to console
    # image: code prints vector representation of image to console
    if mode != "loss" and mode != "image":
        print('mode must be either "loss" or "image"')
        quit()
    fixedModel = torch.load(modelToLoad)

    model = autoencoder().cuda()
    model.load_state_dict(fixedModel)
    criterion = nn.MSELoss()

    print("READY") # Java loops until it sees this special signal
    sys.stdout.flush() # Make sure Java can sense this output before Python blocks waiting for input

    inputImageDimension = 28

    while True:
        # Can't read one line at a time. Too long for console.
        #line = sys.stdin.readline()

        inputList = []
        # Loop through each pixel of image, store in a flat list
        for i in range(inputImageDimension*inputImageDimension):
            inputList.append(float(sys.stdin.readline()))

        #print(len(inputList))

        lv = numpy.array(inputList)
        # Input is already a flat 1D array, so no new view needed
        input = torch.FloatTensor( lv ).cuda()
        output = model(Variable(input))
        loss = criterion(output, input)

        if mode == "image":
            print(json.dumps(output.tolist()))
        else: # should be "loss"
            print(loss.item())
        sys.stdout.flush() # Make Java sense output before blocking on next input