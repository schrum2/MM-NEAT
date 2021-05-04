import sys

if __name__ == "__main__":
    filename = sys.argv[1]
    base = float(sys.argv[2])   # Small positive number added to every non-empty bin's score

    with open(filename,'r') as file:
        for lineList in file:
            lineList = lineList.split()
            gen = int(lineList[0])
            lineSum = 0
            for i in range(1, len(lineList)):
                if lineList[i] != "-Infinity":
                    lineSum += base+float(lineList[i])

            print("{}\t{}".format(gen,lineSum))