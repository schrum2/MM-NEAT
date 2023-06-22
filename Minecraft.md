# Instructions for interacting with the Minecraft server via EvoCraft

Before starting anything, make sure that the correct version of python is installed, and that it is specified in the `my_python_path.txt`. More details on how this is done can be found here: [TUTORIAL.md](TUTORIAL.md)

Once python is set up correctly, the Minecraft server needs to be launched and running. To launch the server for the first time, navigate to the data sub directory. Once here, you should see the directory EvoCraft-py. This should be here on install, but if for whatever reason it isn’t, make a copy of EvoCraft-py-Backup and name the copy EvoCraft-py. Make sure to keep the backup in the data folder.

Once the EvoCraft-py has been made, if needed, open the folder. In order to run the server the eula.txt needs to be marked as true. On a fresh install, it will not be visible, however, running LaunchEvoCraftServer.bat will make it appear (java -jar minecraft_server.1.12.2.jar will also make it appear.) Open eula.txt and change eula from equaling false to true. Save this, then close the txt file. From here, the server can be launched in a couple of different ways. The easiest way is to run LaunchEvoCraftServer.bat. A command window will open and start running. Once a warning about No rcon password set is displayed, the server is running! If the server crashes, double check the eula is set to true, and that there aren't multiple servers running.

If LaunchEvoCraftServer.bat is not in your folder, likely from manually copying over the backup directory, you can also run the server by running it in a PowerShell window. Open the PowerShell while still in the EvoCraft-py folder. Then, you can run java -jar spongevanilla-1.12.2-7.3.0.jar, which will launch the server in PowerShell.

Once the server is running the world starts in survival, and spawns you away from where anything will be generated. So, before any of the shape generating files are run it is helpful to type a few commands to make things easier. In the command window or the Powershell window running the server and type the following commands:

/defaultgamemode creative
/gamerule doWeatherCycle false (keep the weather in one state, then makes the weather clear)
/weather clear
/gamerule doDaylightCycle false (turns off the daylight cycle, then makes it daytime)
/time set day If you join the server before setting creative mode you can use /gamemode creative [Your_Minecraft_Username].
Once you have the server set up, open Minecraft and join the server. (Your Minercraft needs to have a 1.12 installation, and the direct connect server address is "localhost"). Once you join the world type /tp [Your_Minecraft_Username] 10 10 10 which teleports you to the area in which the shapes will be generated. It can also be helpful to op yourself by typing op [Your_Minecraft_Username] into either the command window or the PowerShell window.

Once all of this has been set up, we can start evolving shapes with MAP Elites! Navigate to the `batch` directory and scroll down to the Minecraft batch files. There should be at least 4 directories, all with "Minecraft" at the start of their name. All of these contain lots of batch files to run, but we'll start with `MinecraftAccumulate-NorthSouthVectorCountNegative` in the `MinecraftVectorAccumulate-Flying` directory. Start by launching a PowerShell window, and then typing (or hitting tab) until the desired batch file is chosen. Make sure to type an integer after the file name to give it a seed to generate off of. The command should look something like this in the window: 
* `.\MinecraftAccumulate-NorthSouthVectorCountNegative.bat 10`

You can then press enter to run it. The area will be cleared out in Minecraft, then several fences will be placed to house the shapes (this can take a few minutes, depending on how large the archive/shapes are). Then, once all of initialization is complete, the bins will be populated with shapes. These shapes are being generated and evaluated a few hundred blocks away, where they are given fitness scores. These scores are compared to the shapes in the archive, and if a shape is deemed as more fit than one of the ones in the archive, it is replaced by the more fit individual. So, as shapes are being generated, better and better shapes are replacing the shapes already in the archive.

With each shape, three blocks are also generated:
* The diamond block, which when broken, regenerates the shape. This is useful for when you either want to see how the shape moves, or if it gets broken
* The emerald block, which when broken, deletes the shape from the archive. This allows a new shape to be added if the current one has become stagnant
* The obsidian block, which when broken, forces new individuals to be produced based on the current shape's makeup. This is useful for when you want more shapes that are similar to an already generated shape.
* Also, a gold block is displayed on any individual(s) which have the highest fitness (denoted as champions). This is just a flag to showcase where the "best" individuals are, and breaking it does not have any effect

If anything happens to the server, or your world, and it needs to be reset, run the batch file `ResetServerWorld.bat` and then select all (by typing "a" into the command window that appears). This will give you a fresh world by copying in the files from the backup directory, however, all of your settings and inventory items will be saved. Make sure the server is not running when it is run, otherwise it will not reset.

From here, all of the other batch files can also be run, which all have different focuses on outcomes. Happy generating!

For more help/information on EvoCraft, visit the [**Evocraft-py repository**](https://github.com/real-itu/Evocraft-py)
