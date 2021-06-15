# From : https://discuss.pytorch.org/t/custom-image-dataset-for-autoencoder/16118/2
# And: http://5.9.10.113/67219558/conv-autoencoder-on-rgb-images-not-working-in-pytorch

import os
import cv2
import torch
import numpy as np

class DatasetLoader(torch.utils.data.Dataset):
    def __init__(self, root, transforms=None):
        #print(root)
        self.root = root
        self.transforms = transforms
        self.imgs = list(sorted(os.listdir(root)))
        #print(self.imgs)        

    def __getitem__(self, idx):
        img_path = os.path.join(self.root, self.imgs[idx])
        #print(img_path)
        img=cv2.imread(img_path)
        img=cv2.cvtColor(img,cv2.COLOR_BGR2GRAY) 
        img=img/255.0

        #print(img)
    
        img = img.astype(np.float32)

        #print(img)

        return torch.from_numpy(img)

    def __len__(self):
        return len(self.imgs)