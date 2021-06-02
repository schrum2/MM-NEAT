import torch
from torch.autograd import Variable

import sys
import json
import numpy
from model import autoencoder

if __name__ == '__main__':
    
    modelToLoad = 'sim_autoencoder.pth'
    fixedModel = torch.load(modelToLoad, map_location=lambda storage, loc: storage)

    model = autoencoder().cuda()
    model.load_state_dict(fixedModel)

    print("READY") # Java loops until it sees this special signal
    sys.stdout.flush() # Make sure Java can sense this output before Python blocks waiting for input

    while True:
        line = sys.stdin.readline()

        lv = numpy.array(json.loads(line))
        # Input is already a flat 1D array, so no new view needed
        input = torch.FloatTensor( lv ) 
        output = model(Variable(input, volatile=True))

        print(json.dumps(output.tolist()))
        sys.stdout.flush() # Make Java sense output before blocking on next input