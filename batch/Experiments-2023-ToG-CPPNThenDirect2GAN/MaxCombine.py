
import sys

if __name__ == '__main__':
    # Verify proper command line arguments
    if len(sys.argv) != 4:
        print("Provide two input file names and a target file name as command line parameters")
        print("Example: python <f1.txt> <f2.txt> <target.txt>")
        quit()

    f1name = sys.argv[1]
    f2name = sys.argv[2]
    targetName = sys.argv[3]

    f1 = open(f1name,'r')
    f2 = open(f2name,'r')
    target = open(targetName,'w')

    line1 = f1.readline()
    line2 = f2.readline()

    while line1 and line2:

        tokens1 = line1.split()
        tokens2 = line2.split()

        nums1 = list(map(lambda x: float('-inf') if x.strip() == "X" else float(x),tokens1))
        nums2 = list(map(lambda x: float('-inf') if x.strip() == "X" else float(x),tokens2))

        zipped = list(zip(nums1,nums2))
        maxes = list(map(max,zipped))

        #print(line1)
        #print(line2)
        #print(maxes)
        #input()

        for x in maxes:
            if x == float('-inf'):
                target.write("X") # Used to be "-Infinity" here
            else:    
                target.write("{}".format(x))
            target.write("\t")

        target.write("\n")

        line1 = f1.readline()
        line2 = f2.readline()

    f1.close()
    f2.close()
    target.close()
