import numpy as np
import torch
import torch.nn as nn
import torch.nn.functional as F
import torch.optim as optim
import matplotlib.pyplot as plt
from torch.utils.data.sampler import SubsetRandomSampler
from torch.utils.data import DataLoader
from torchvision import datasets, transforms
import torch.nn as nn
import myColorData
import sys
# import ConvAutoencoder
from ConvAutoencoder import ConvAutoencoder

if __name__ == '__main__':

    trainingImagesDirectory = sys.argv[1]
    pthFileToSave = sys.argv[2]

    #Converting data to torch.FloatTensor
    transform = transforms.ToTensor()
    batch_size = 20

    train_data = myColorData.CustomImageDataSet(trainingImagesDirectory, transform)

    # Why does it want us to drop_last?
    #train_dataloader = DataLoader(train_data , batch_size=batch_size, shuffle=False, num_workers=4, drop_last=True)
    train_dataloader = DataLoader(train_data , batch_size=batch_size, shuffle=False, num_workers=0)

    #Utility functions to un-normalize and display an image
    def imshow(img):
        img = img / 2 + 0.5  
        plt.imshow(np.transpose(img, (1, 2, 0))) 

    #Obtain one batch of training images
    dataiter = iter(train_dataloader)
    images = dataiter.next()
    images = images.numpy() # convert images to numpy for display

    #Plot the images
    fig = plt.figure(figsize=(8, 8))
    # display 20 images
    for idx in np.arange(9):
        ax = fig.add_subplot(3, 3, idx+1, xticks=[], yticks=[])
        imshow(images[idx])

    #Instantiate the model
    model = ConvAutoencoder()
    print(model)

    #Loss function
    criterion = nn.BCELoss()

    #Optimizer
    optimizer = torch.optim.Adam(model.parameters(), lr=0.001)

    def get_device():
        if torch.cuda.is_available():
            device = 'cuda:0'
        else:
            device = 'cpu'
        return device

    device = get_device()
    print(device)
    model.to(device)

    #Epochs
    n_epochs = 1000

    for epoch in range(1, n_epochs+1):
        # monitor training loss
        train_loss = 0.0

        #Training
        for data in train_dataloader:
            images = data
            images = images.to(device)
            optimizer.zero_grad()
            outputs = model(images)
            loss = criterion(outputs, images)
            loss.backward()
            optimizer.step()
            train_loss += loss.item()*images.size(0)
          
        train_loss = train_loss/len(train_dataloader)
        print('Epoch: {} \tTraining Loss: {:.6f}'.format(epoch, train_loss))

    # For now: Test on Training Data
    test_loader = train_dataloader

    #Batch of test images
    dataiter = iter(test_loader)
    images = dataiter.next()

    #Sample outputs
    output = model(images.to(device))
    images = images.numpy()

    output = output.view(batch_size, 3, 28, 28)
    output = output.detach().cpu().numpy()

    #Original Images
    print("Original Images")
    fig, axes = plt.subplots(nrows=1, ncols=5, sharex=True, sharey=True, figsize=(12,4))
    for idx in np.arange(5):
        ax = fig.add_subplot(1, 5, idx+1, xticks=[], yticks=[])
        imshow(images[idx])
        #ax.set_title(classes[labels[idx]])
    plt.show()

    #Reconstructed Images
    print('Reconstructed Images')
    fig, axes = plt.subplots(nrows=1, ncols=5, sharex=True, sharey=True, figsize=(12,4))
    for idx in np.arange(5):
        ax = fig.add_subplot(1, 5, idx+1, xticks=[], yticks=[])
        imshow(output[idx])
        #ax.set_title(classes[labels[idx]])
    plt.show() 

    torch.save(model.state_dict(), pthFileToSave)

    print("END")
