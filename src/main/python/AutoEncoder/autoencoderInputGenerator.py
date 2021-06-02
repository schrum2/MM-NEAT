import torch
from torch.autograd import Variable

import sys
import json
import numpy
from model import autoencoder

if __name__ == '__main__':
    
    modelToLoad = 'sim_autoencoder.pth'
    nz = int(sys.argv[2])
    z_dims = int(sys.argv[3])
    out_width = int(sys.argv[4])
    out_height = int(sys.argv[5])
    fixedModel = OrderedDict() ## Use torch.load

    imageSize = 32
    ngf = 64
    ngpu = 1
    n_extra_layers = 0
    batchSize = 1

    model = autoencoder().cuda()
    model.load_state_dict(fixedModel)

    while True:
        line = sys.stdin.readline()

        lv = numpy.array(json.loads(line))
        latent_vector = torch.FloatTensor( lv ).view(batchSize, nz, 1, 1) 
        levels = model(Variable(latent_vector, volatile=True))

print(json.dumps(level[0].tolist()))
sys.stdout.flush() # Make Java sense output before blocking on next input