#!/usr/bin/env python

"""
plot position data from a time, position, rotation file
"""

import numpy as np
from mpl_toolkits.mplot3d import axes3d, Axes3D
import matplotlib.pyplot as plt
import sys
from utils import PoseVisitor, get_level

class PosePlotter(PoseVisitor):
    def before(self, levelstr):
        self.level = get_level(levelstr)
        self.fig = plt.figure()
        self.ax = Axes3D(self.fig)
        self.ax.hold(True)
        self.empty = True
    def for_each_segment(self, segment, label, color):
        if not label:
            label = '_nolegend_' # same player as before, use the same label
        segment = np.array(segment)
        self.ax.plot(segment[:,1], segment[:,2], segment[:,3], label=label, color=color)
        self.empty = False
    def for_each_game(self, levelstr):
        if not self.empty:
            self.ax.set_xlabel('X')
            self.ax.set_ylabel('Y')
            self.ax.set_zlabel('Z')
            self.ax.legend()
            self.fig.suptitle(self.level)
            plt.savefig(self.level + '.png', dpi=300)
        else:
            plt.close(self.fig)
        self.empty = True
    def after(self):
        #plt.show()
        pass

def main():
    plotter = PosePlotter()
    if len(sys.argv) > 1:
        plotter.process(sys.argv[1])
    else:
        plotter.process()

if __name__ == "__main__":
    main()
