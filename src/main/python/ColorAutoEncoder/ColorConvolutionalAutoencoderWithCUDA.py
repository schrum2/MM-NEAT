import numpy as np
import torch
import torch.nn as nn
import torch.nn.functional as F
import torch.optim as optim
import matplotlib.pyplot as plt
from torch.utils.data.sampler import SubsetRandomSampler
from torch.utils.data import DataLoader
from torchvision import datasets, transforms
import matplotlib.pyplot as plt
import torch.nn as nn
import torch.nn.functional as F
import myColorData

#if __name__ == '__main__':
#    trainingImagesDirectory = sys.argv[1]
#
#    batch_size = 128

#training_data = datasets.FashionMNIST(
#    root="data",
#    train=True,
#    download=True,
#    transform=ToTensor()
#)

#test_data = datasets.FashionMNIST(
#    root="data",
#    train=False,
#    download=True,
#    transform=ToTensor()
#)



# Display image and label.
#train_features, train_labels = next(iter(train_dataloader))
#print(f"Feature batch shape: {train_features.size()}")
#print(f"Labels batch shape: {train_labels.size()}")
#img = train_features[0].squeeze()
#label = train_labels[0]
#plt.imshow(img, cmap="gray")
#plt.show()
#print(f"Label: {label}")

#Converting data to torch.FloatTensor
transform = transforms.ToTensor()
batch_size = 20

# Download the training and test datasets
#train_data = datasets.CIFAR10(root='data', train=True, download=True, transform=transform)

#test_data = datasets.CIFAR10(root='data', train=False, download=True, transform=transform)

train_data = myColorData.CustomImageDataSet("C:\\Users\\wickera\\GitHub\\MM-NEAT\\src\\main\\python\\ColorAutoEncoder\\ColorTrainingSet", transform)

# Download the Picbreeder color images training set
#dataset = mydata.DatasetLoader(trainingImagesDirectory)

#dataloader = DataLoader(dataset, batch_size=batch_size, shuffle=True)

#train_dataloader = DataLoader(training_data, batch_size=128, shuffle=True)

# Why does it want us to drop_last?
#train_dataloader = DataLoader(train_data , batch_size=batch_size, shuffle=False, num_workers=4, drop_last=True)
train_dataloader = DataLoader(train_data , batch_size=batch_size, shuffle=False, num_workers=0)

#test_dataloader = DataLoader(test_data, batch_size=128, shuffle=True)

#Prepare data loaders
#train_loader = torch.utils.data.DataLoader(train_data, batch_size=32, num_workers=0)
#test_loader = torch.utils.data.DataLoader(test_data, batch_size=32, num_workers=0)

#Utility functions to un-normalize and display an image
def imshow(img):
    img = img / 2 + 0.5  
    plt.imshow(np.transpose(img, (1, 2, 0))) 

 
#Define the image classes
#classes = ['airplane', 'automobile', 'bird', 'cat', 'deer', 'dog', 'frog', 'horse', 'ship', 'truck']

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
    #ax.set_title(classes[labels[idx]])

#Define the Convolutional Autoencoder
class ConvAutoencoder(nn.Module):
    def __init__(self):
        super(ConvAutoencoder, self).__init__()
       
        #Encoder
        self.conv1 = nn.Conv2d(3, 16, 3, padding=1)  
        self.conv2 = nn.Conv2d(16, 4, 3, padding=1)
        self.pool = nn.MaxPool2d(2, 2)
       
        #Decoder
        self.t_conv1 = nn.ConvTranspose2d(4, 16, 2, stride=2)
        self.t_conv2 = nn.ConvTranspose2d(16, 3, 2, stride=2)


    def forward(self, x):
        x = F.relu(self.conv1(x))
        x = self.pool(x)
        x = F.relu(self.conv2(x))
        x = self.pool(x)
        x = F.relu(self.t_conv1(x))
        x = F.sigmoid(self.t_conv2(x))
              
        return x


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
n_epochs = 200

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
