import torch
from torch.autograd import Variable

import sys
import json
import numpy
from model import autoencoder

if __name__ == '__main__':
    
    modelToLoad = sys.argv[1] #'sim_autoencoder.pth'
    fixedModel = torch.load(modelToLoad)

    model = autoencoder().cuda()
    model.load_state_dict(fixedModel)

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

        print(json.dumps(output.tolist()))
        sys.stdout.flush() # Make Java sense output before blocking on next input