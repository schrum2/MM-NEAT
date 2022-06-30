# Instructions for interacting with the Minecraft server via EvoCraft

Before evolving Minecraft shapes, the Minecraft server needs to be launched and running. To launch the server for the first time, navigate to 'data' sub directory. Once here, you should see two directories:'EvoCraft-py-Backup' and 'EvoCraft-py'. If 'EvoCraft-py' is not there ,make a copy of this file and name the copy 'EvoCraft-py'. Make sure to keep the bakcup in the data folder, as if anything happens to the server, this file can be copied again for a fresh new world. 

Once the 'EvoCraft-py' has been made, if needed, open the folder there should be 'eula.txt' agreement there, but if there isn't, you can double click on 'minecraft_server.1.12.2.jar' this will make it appear. open 'eula.txt' and change eula from it equaling false to true. Save this, then close the txt file. From here, the server can be launched in a couple of different ways. The easiest way is to run 'LaunchEvoCraftServer.bat'. A command window will open and start running. Once " No rcon password set in 'C:\schrum2MM-NEAT\MM-NEAT\data\EvoCraft-py\server.properties', rcon disabled!" is displayed, the server is running! If the server crashes, double check the eula is set to true, and that there aren't multiple server running. 

If 'LaunchEvoCraftServer.bat' is not in your folder, you can also run the server by running it in a powershell window. Open the powershell by hold shift and left clicking in file explorer, and clicking "open PowerShell window here" while still in the 'EvoCraft-py" folder. Then, you can run "java -jar spongevanilla-1.12.2-7.3.0.jar", which will launch the server in PowerShell. 

Once the server is running, open Minecraft and join the server. (Your Minercraft needs to have a 1.12 installation, and the direct connect server address is "localhost"). The world starts in survival, and spawns you away from where anything will be generated. So, before any of the shape generating files are run, it's a good idea to run some commands in Minecraft to make everythinf easier. In order to do this, you need to op yourself by typing 'op [Your_Minecraft_Username]" into either the command window or the PowerShell window, whichever you are running the server on. From here, return to minecraft and type the follwong commands:
* /gamemode creative 
* /tp 10 10 10 (This teleports you to the area in which the shapes will be generated
* /weather clear
* /gamerule doWeatherCycle false (Makes the weather clear, then keeps it that way)
* /time set day
* /gamerule doDaylightCycle false (Makes it day time, then keeps it that way)

Once all of this has been set up, we can start evolving shapes with MAP Elites! Navigate to the 'batch' directory and scroll down to the Minecraft batch files. There should be at least 4 directories, all with "Minecraft" at the start of their name. All of these contain lots of batch files to run, but we'll start with 'MinecraftAccumulate-NorthSouthVectorCountNegative' in the 'MinecraftVectorAccumulate-Flying' directory. Start by launching a PowerShell window, and then typing (or hitting tab) until the desired batch file is chosen. make sure to type a integer after file name to give it a random seed to generate off of. The command should look like this in the window: 
* .\MinecraftAccumulate-NorthSouthVectorCountNegative.bat 10

You can then press enter to run it. The area will be cleared out in Minecraft, then several fences will be placed to house the shapes, and then, once all of initailization is complete, the bins will be populated with shapes. These shapes are being generated and evaluated a few hundred blocks away, and they are given fitness scores. These scores are compared to the shapes in the archive, and  if a shape is deemed as more fit than once of the ones in the archive, it is repolaced by the more fit individual. So, as shapes are being generated, beter and better shapes are replacing the shapes already in the archive.

With each block, three blocks are generated:
* The diamond block, which when broken, regenerates the shape. This is useful for when you either want to see how the shape moves, or if it gets broken
* The emerald block, which whem broken, deletes the shape from the archive. This allows a new shape to be added if the current one has become stagnant
* The obsidain block, which when broken, forces new indidiuals to be produced based on the current shape's make up. This is useful for when you want more shapes that are similar to an already generated shape.
* Also, a gold block is displayed on any individual(s) which have the highest itness (denoted as champions). This is just a flag to showcase where the "best" individuals are, and breaking it does not have any effect

From here, all of the batch files can be run, which all have different focuses. Happy generating!
