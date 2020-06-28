import torch
import torch.nn as nn
import torch.nn.parallel

class CDCGAN_D(nn.Module):
    def __init__(self, isize, nz, nc, ndf, ngpu, num_classes, n_extra_layers=0):
        super(CDCGAN_D, self).__init__()
        self.ngpu = ngpu
        assert isize % 16 == 0, "isize has to be a multiple of 16"

        initial = nn.ModuleList()
        # input is nc x isize x isize
        initial.add_module('initial:conv:{0}-{1}'.format(nc, ndf),
                           nn.Conv2d(nc, ndf, 4, 2, 1, bias=False))
        initial.add_module('initial:relu:{0}'.format(ndf),
                           nn.LeakyReLU(0.2, inplace=True))

        classList = nn.ModuleList()
        classList.add_module('embedclass:{0}-{1}:convt'.format(num_classes, ndf),
                        nn.ConvTranspose2d(num_classes, ndf, 16, 1, 0, bias=False))
        classList.add_module('embedclass:{0}:batchnorm'.format(ndf),
                        nn.BatchNorm2d(ndf))
        classList.add_module('embedclass:{0}:relu'.format(ndf),
                        nn.LeakyReLU(0.2, inplace=True))

        csize, cndf = isize / 2, ndf

        # Added this size doubling for the conditional GAN because the combination of
        # Input and class label embedding makes input at this layer twice as big
        cndf = cndf * 2

        main = nn.ModuleList()
        # Extra layers
        for t in range(n_extra_layers):
            main.add_module('extra-layers-{0}:{1}:conv'.format(t, cndf),
                            nn.Conv2d(cndf, cndf, 3, 1, 1, bias=False))
            main.add_module('extra-layers-{0}:{1}:batchnorm'.format(t, cndf),
                            nn.BatchNorm2d(cndf))
            main.add_module('extra-layers-{0}:{1}:relu'.format(t, cndf),
                            nn.LeakyReLU(0.2, inplace=True))

        while csize > 4:
            in_feat = cndf
            out_feat = cndf * 2
            main.add_module('pyramid:{0}-{1}:conv'.format(in_feat, out_feat),
                            nn.Conv2d(in_feat, out_feat, 4, 2, 1, bias=False))
            main.add_module('pyramid:{0}:batchnorm'.format(out_feat),
                            nn.BatchNorm2d(out_feat))
            main.add_module('pyramid:{0}:relu'.format(out_feat),
                            nn.LeakyReLU(0.2, inplace=True))
            cndf = cndf * 2
            csize = csize / 2

        # state size. K x 4 x 4
        main.add_module('final:{0}-{1}:conv'.format(cndf, 1),
                        nn.Conv2d(cndf, 1, 4, 1, 0, bias=False))

        #print(initial)
        #print(classList)
        #print(main)

        self.classList = classList
        self.initial = initial
        self.main = main

    def forward(self, input, labels):
        x = input
        #print(x.shape)
        for m in self.initial:
            x = m.forward(x)
        y = labels
        #print(y.shape)
        for m in self.classList:
            y = m.forward(y)

        #print(x.shape)
        #print(y.shape)

        x = torch.cat([x,y], 1)
        for m in self.main:
            x = m.forward(x)
        
        #output = self.main(input)
            
        output = x.mean(0)
        return output.view(1)

class CDCGAN_G(nn.Module):
    def __init__(self, isize, nz, nc, ngf, ngpu, num_classes, n_extra_layers=0):
        super(CDCGAN_G, self).__init__()
        self.ngpu = ngpu
        assert isize % 16 == 0, "isize has to be a multiple of 16"

        cngf, tisize = ngf//2, 4
        while tisize != isize:
            cngf = cngf * 2
            tisize = tisize * 2

        initial = nn.ModuleList()
        # input is Z, going into a convolution
        initial.add_module('initial:{0}-{1}:convt'.format(nz, cngf),
                        nn.ConvTranspose2d(nz, cngf, 4, 1, 0, bias=False))
        initial.add_module('initial:{0}:batchnorm'.format(cngf),
                        nn.BatchNorm2d(cngf))
        initial.add_module('initial:{0}:relu'.format(cngf),
                        nn.ReLU(True))

        classList = nn.ModuleList()
        classList.add_module('embedclass:{0}-{1}:convt'.format(num_classes, cngf),
                        nn.ConvTranspose2d(num_classes, cngf, 4, 1, 0, bias=False))
        classList.add_module('embedclass:{0}:batchnorm'.format(cngf),
                        nn.BatchNorm2d(cngf))
        classList.add_module('embedclass:{0}:relu'.format(cngf),
                        nn.ReLU(True))


        csize, cndf = 4, cngf
        main = nn.ModuleList()

        # This size had to double because the combination of inputs AND class labels increased input size
        cngf = cngf*2

        while csize < isize//2:
            main.add_module('pyramid:{0}-{1}:convt'.format(cngf, cngf//2),
                            nn.ConvTranspose2d(cngf, cngf//2, 4, 2, 1, bias=False))
            main.add_module('pyramid:{0}:batchnorm'.format(cngf//2),
                            nn.BatchNorm2d(cngf//2))
            main.add_module('pyramid:{0}:relu'.format(cngf//2),
                            nn.ReLU(True))
            cngf = cngf // 2
            csize = csize * 2

        # Extra layers
        for t in range(n_extra_layers):
            main.add_module('extra-layers-{0}:{1}:conv'.format(t, cngf),
                            nn.Conv2d(cngf, cngf, 3, 1, 1, bias=False))
            main.add_module('extra-layers-{0}:{1}:batchnorm'.format(t, cngf),
                            nn.BatchNorm2d(cngf))
            main.add_module('extra-layers-{0}:{1}:relu'.format(t, cngf),
                            nn.ReLU(True))

        main.add_module('final:{0}-{1}:convt'.format(cngf, nc),
                        nn.ConvTranspose2d(cngf, nc, 4, 2, 1, bias=False))
        main.add_module('final:{0}:tanh'.format(nc),
                        nn.ReLU())#nn.Softmax(1))    #Was TANH nn.Tanh())#

        # Useful for troubleshooting layer sizes
        #print(initial)
        #print(classList)
        #print(main)

        self.classList = classList
        self.initial = initial
        self.main = main

    def forward(self, input, labels):
        x = input
        #print(x.shape)
        for m in self.initial:
            x = m.forward(x)
        y = labels
        #print(y.shape)
        for m in self.classList:
            y = m.forward(y)
       
        #print(x.shape)
        #print(y.shape)

        x = torch.cat([x,y], 1)

        for m in self.main:
            x = m.forward(x)
        
        #output = self.main(input)

        #print (output[0,:,0,0])
        #exit()
        return x
