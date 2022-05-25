
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

        # TODO: Commands


        # print response to console (to be read by Java code)
        print("TODO")
        sys.stdout.flush() # Make Java sense output before blocking on next input
