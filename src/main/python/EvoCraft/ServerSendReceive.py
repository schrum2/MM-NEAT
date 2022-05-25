
import sys

import grpc
import minecraft_pb2_grpc
from minecraft_pb2 import *

if __name__ == '__main__':

    # Connect to Minecraft server
    channel = grpc.insecure_channel('localhost:5001')
    client = minecraft_pb2_grpc.MinecraftServiceStub(channel)

    print("READY") # Java loops until it sees this special signal
    sys.stdout.flush() # Make sure Java can sense this output before Python blocks waiting for input

    while True:
        # Read input command from console (Java code sends commands here)
        line = sys.stdin.readline()

        tokens = line.split()
        command = tokens[0]
        parameters = list(map(int,tokens[1:]))
        parameters_copy = parameters.copy()
        try:
            # Commands
            if command == "spawnBlocks":
                # client.spawnBlocks(Blocks(blocks=shape)) where shape is a list of Blocks
                # Each block is a sequence of 5 integers: x y z type orientation
                # Use pop() to get the values, but they will be in reverse
                shape = []
                while parameters != []:
                    orientation = parameters.pop()
                    type = parameters.pop()
                    z = parameters.pop()
                    y = parameters.pop()
                    x = parameters.pop()
                    shape.append(Block(position=Point(x=x, y=y, z=z), type=type, orientation=orientation))
                    # Will crash with an error if there are not 5 ints per block in the input stream

                client.spawnBlocks(Blocks(blocks=shape))

            elif command == "readCube":
                # client.readCube(Cube(min=Point(x=xmin, y=ymin, z=zmin), max=Point(x=xmax, y=ymax, z=zmax) )) 
                # where (xmin,ymin,zmin) and (xmax,ymax,zmax) bound the Cube
                # The parameters are: xmin ymin zmin xmax ymax zmax
                # Read in reverse using pop()

                zmax = parameters.pop()
                ymax = parameters.pop()
                xmax = parameters.pop()
                zmin = parameters.pop()
                ymin = parameters.pop()
                xmin = parameters.pop()

                result = client.readCube(Cube(min=Point(x=xmin, y=ymin, z=zmin), max=Point(x=xmax, y=ymax, z=zmax) )) 

                # print response to console (to be read by Java code)
                for block in result.blocks:
                    # Print format: x y z type
                    # All results printed on a single line
                    x = block.position.x
                    y = block.position.y
                    z = block.position.z
                    type = block.type
                    print("{} {} {} {} ".format(x,y,z,type),end="")
                
                print()
                sys.stdout.flush() # Make Java sense output before blocking on next input

            elif command == "fillCube":      
                # client.fillCube(FillCubeRequest(cube=Cube(min=Point(x=xmin, y=ymin, z=zmin), max=Point(x=xmax, y=ymax, z=zmax) ), type=filltype )) 
                # where (xmin,ymin,zmin) and (xmax,ymax,zmax) bound the Cube and filltype is the type
                # The parameters are: xmin ymin zmin xmax ymax zmax type
                # Read in reverse using pop()
                type = parameters.pop()
                zmax = parameters.pop()
                ymax = parameters.pop()
                xmax = parameters.pop()
                zmin = parameters.pop()
                ymin = parameters.pop()
                xmin = parameters.pop()
                
                client.fillCube(FillCubeRequest(cube=Cube(min=Point(x=xmin, y=ymin, z=zmin), max=Point(x=xmax, y=ymax, z=zmax) ), type=type ))

            elif command == "quit":
                # Gracefully quit
                quit()
            else:
                print("Illegal command. Only spawnBlocks, readCube, and fillCube are recognized. Not {}".format(command))
                quit()

        except IndexError:
            print("Wrong number of parameters sent to the command {}. Only sent {}".format(command,parameters_copy))
            quit()

