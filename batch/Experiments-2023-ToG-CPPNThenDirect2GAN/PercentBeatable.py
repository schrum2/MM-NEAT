import sys

# Call with a MAP Elites log file (all archive scores)
if __name__ == "__main__":
    filename = sys.argv[1]

    print("#Gen\tBeatable\tUnbeatable\tPercentBeatable")
    with open(filename,'r') as file:
        for lineList in file:
            lineList = lineList.split()
            gen = int(lineList[0])
            
            beatable = 0
            unbeatable = 0
            
            for i in range(1, len(lineList)):
                if lineList[i].strip() != "X" and lineList[i].strip() != "-Infinity":
                    score = float(lineList[i])
                    if score > 0: beatable +=1
                    else: unbeatable += 1

            print("{}\t{}\t{}\t{}".format(gen,beatable,unbeatable,beatable/(beatable+unbeatable)))