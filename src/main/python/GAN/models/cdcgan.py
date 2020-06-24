import torch
import torch.nn as nn
import torch.nn.parallel

class DCGAN_D(nn.Module):
    def __init__(self, isize, nz, nc, ndf, ngpu, n_extra_layers=0):
        super(DCGAN_D, self).__init__()

        self.isize = isize
        self.nz = nz
        self.nc = nc
        self.ndf = ndf
        self.ngpu = ngpu
        self.n_extra_layers = n_extra_layers

        assert isize % 16 == 0, "isize has to be a multiple of 16"

        main = dict()
        # input is nc x isize x isize
        main['initial:conv:{0}-{1}'.format(nc, ndf)] = nn.Conv2d(nc, ndf, 4, 2, 1, bias=False)
        main['initial:relu:{0}'.format(ndf)] = nn.LeakyReLU(0.2, inplace=True)
        csize, cndf = isize / 2, ndf

        # Extra layers
        for t in range(n_extra_layers):
            main['extra-layers-{0}:{1}:conv'.format(t, cndf)] = nn.Conv2d(cndf, cndf, 3, 1, 1, bias=False)
            main['extra-layers-{0}:{1}:batchnorm'.format(t, cndf)] = nn.BatchNorm2d(cndf)
            main['extra-layers-{0}:{1}:relu'.format(t, cndf)] = nn.LeakyReLU(0.2, inplace=True)

        while csize > 4:
            in_feat = cndf
            out_feat = cndf * 2
            main['pyramid:{0}-{1}:conv'.format(in_feat, out_feat)] = nn.Conv2d(in_feat, out_feat, 4, 2, 1, bias=False)
            main['pyramid:{0}:batchnorm'.format(out_feat)] = nn.BatchNorm2d(out_feat)
            main['pyramid:{0}:relu'.format(out_feat)] = nn.LeakyReLU(0.2, inplace=True)
            cndf = cndf * 2
            csize = csize / 2

        # state size. K x 4 x 4
        main['final:{0}-{1}:conv'.format(cndf, 1)] = nn.Conv2d(cndf, 1, 4, 1, 0, bias=False)
        self.main = main


    def forward(self, input):
        isize = self.isize
        nc = self.nc
        ndf = self.ndf
        n_extra_layers = self.n_extra_layers

        x = self.main['initial:conv:{0}-{1}'.format(nc, ndf)](input)
        x = self.main['initial:relu:{0}'.format(ndf)](x)
        csize, cndf = isize / 2, ndf

        # Extra layers
        for t in range(n_extra_layers):
            x = self.main['extra-layers-{0}:{1}:conv'.format(t, cndf)](x)
            x = self.main['extra-layers-{0}:{1}:batchnorm'.format(t, cndf)](x)
            x = self.main['extra-layers-{0}:{1}:relu'.format(t, cndf)](x)

        while csize > 4:
            in_feat = cndf
            out_feat = cndf * 2
            x = self.main['pyramid:{0}-{1}:conv'.format(in_feat, out_feat)](x)
            x = self.main['pyramid:{0}:batchnorm'.format(out_feat)](x)
            x = self.main['pyramid:{0}:relu'.format(out_feat)](x)
            cndf = cndf * 2
            csize = csize / 2

        # state size. K x 4 x 4
        output = self.main['final:{0}-{1}:conv'.format(cndf, 1)](x)            
        output = output.mean(0)
        return output.view(1)

class DCGAN_G(nn.Module):
    def __init__(self, isize, nz, nc, ngf, ngpu, n_extra_layers=0):
        super(DCGAN_G, self).__init__()
        self.ngpu = ngpu
        assert isize % 16 == 0, "isize has to be a multiple of 16"

        cngf, tisize = ngf//2, 4
        while tisize != isize:
            cngf = cngf * 2
            tisize = tisize * 2

        main = dict()
        # input is Z, going into a convolution
        main['initial:{0}-{1}:convt'.format(nz, cngf)] = nn.ConvTranspose2d(nz, cngf, 4, 1, 0, bias=False)
        main['initial:{0}:batchnorm'.format(cngf)] = nn.BatchNorm2d(cngf)
        main['initial:{0}:relu'.format(cngf)] = nn.ReLU(True)

        csize, cndf = 4, cngf
        while csize < isize//2:
            main['pyramid:{0}-{1}:convt'.format(cngf, cngf//2)] = nn.ConvTranspose2d(cngf, cngf//2, 4, 2, 1, bias=False)
            main['pyramid:{0}:batchnorm'.format(cngf//2)] = nn.BatchNorm2d(cngf//2)
            main['pyramid:{0}:relu'.format(cngf//2)] = nn.ReLU(True)
            cngf = cngf // 2
            csize = csize * 2

        # Extra layers
        for t in range(n_extra_layers):
            main['extra-layers-{0}:{1}:conv'.format(t, cngf)] = nn.Conv2d(cngf, cngf, 3, 1, 1, bias=False)
            main['extra-layers-{0}:{1}:batchnorm'.format(t, cngf)] = nn.BatchNorm2d(cngf)
            main['extra-layers-{0}:{1}:relu'.format(t, cngf)] = nn.ReLU(True)

        main['final:{0}-{1}:convt'.format(cngf, nc)] = nn.ConvTranspose2d(cngf, nc, 4, 2, 1, bias=False)
        main['final:{0}:tanh'.format(nc)] = nn.ReLU()#nn.Softmax(1))    #Was TANH nn.Tanh())#
        self.main = main

    def forward(self, input):

        # TODO: Modify this the same way the discriminator's forward method was modified

        output = self.main(input)

        #print (output[0,:,0,0])
        #exit()
        return output 
