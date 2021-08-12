
# Give file path to a lineage log file, such as 
# "ZeldaDungeonsDistinctBTRooms-CPPNThenDirect2GAN0_Lineage_log.txt"
# And identify a terminal ID number, and the script will
# use the log file to list a lineage leading up to the terminal value.
# Note that if crossover occurred at any point in the lineage, then only
# one parent's lineage will be traced.

import sys

#   Usage: python lineage.py <lineage log> <genome id>
# Example: python lineage.py ../../zeldadungeonsdistinctbtrooms/CPPNThenDirect2GAN0/ZeldaDungeonsDistinctBTRooms-CPPNThenDirect2GAN0_Lineage_log.txt 96130

if __name__ == "__main__":
    lineage = sys.argv[1]
    terminal_id = sys.argv[2]
    sequence = [terminal_id] # Will store sequence of ids in lineage

    print("Lineage of {} in {}".format(terminal_id, lineage))

    with open(lineage) as file:
        lines = file.readlines()
        for line in reversed(lines):
            shattered = line.strip('\n').split(" ")
            #print("Checking", shattered)
            last_token = shattered[len(shattered) - 1]
            if last_token == terminal_id: # parent(s) found
                if len(shattered) == 3: # Asexual reproduction
                    # One genome produced another, so add to front of lineage and search for the parent's parent
                    sequence.insert(0,shattered[0]) 
                    terminal_id = shattered[0]
                else: # Crossover
                    # Two genomes produced one child. Add both to lineage
                    sequence.insert(0,"{} X {}".format(shattered[0],shattered[2])) 
                    # But arbitrarily choose to seek only the parent of the first
                    terminal_id = shattered[0]

    print(sequence)