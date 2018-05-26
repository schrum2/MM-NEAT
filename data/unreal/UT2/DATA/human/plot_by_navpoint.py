#!/usr/bin/env python

"""
plot navpoints (needs Levels and level-counts.txt)
"""

import numpy as np
from mpl_toolkits.mplot3d import axes3d, Axes3D
import matplotlib.pyplot as plt
from matplotlib import cm
from matplotlib.colors import colorConverter
import sys
from utils import IndexedPoseVisitor, get_level
import sqlite3
import itertools

def get_navpoints(level):
    navpoints = {}
    links = []
    conn = sqlite3.connect('../navpoints.db')
    c = conn.cursor()
    c.execute('select id, unreal_id, x, y, z from navpoint where level=?', (level,) )
    for row in c:
        navpoints[int(row[0])] = (row[1], tuple([float(x) for x in row[2:]]))
    c.execute('select origin, destination from link')
    for row in c:
        origin = int(row[0])
        dest = int(row[1])
        if origin in navpoints and dest in navpoints:
            links.append( (origin, dest) )
    c.close()
    conn.close()
    counts = {}
    with open('NavPointIndex.dat') as f:
        for line in f:
            name, id, path_count, point_count = line.strip().split()
            if name.startswith(level):
                id, path_count, point_count = int(id), int(path_count), int(point_count)
                counts[id] = (path_count, point_count)
    return (navpoints, links, counts)

navpointcolors = {}

COLORSOURCE = itertools.cycle([colorConverter.to_rgb(x) for x in ['b', 'g', 'r', 'c', 'm', 'y', 'k']])
    
class NavpointPlotter(IndexedPoseVisitor):
    def before(self, levelstr):
        self.level = get_level(levelstr)
        self.fig = plt.figure()
        self.ax = Axes3D(self.fig)
        self.ax.hold(True)
        self.empty = True
        (self.navpoints, self.links, self.counts) = get_navpoints(self.level)
    def plot_navpoints(self):
        for npid in self.navpoints:
            name, (x,y,z) = self.navpoints[npid]
            if npid in navpointcolors:
                color = navpointcolors[npid]
            else:
                color = colorConverter.to_rgb('white')
            s = 7
            label = self.navpoints[npid][0].split('.')[1]
            self.ax.plot( [x], [y], [z], markerfacecolor=color, markersize=s, marker='s', label=label )
    def for_each_segment(self, segment, label, color):
        c = []
        for t, x, y, z, rx, ry, rz, npid in segment:
            if npid in navpointcolors:
                color = navpointcolors[npid]
            else:
                color = COLORSOURCE.next()
                navpointcolors[npid] = color
            c.append(color)
        xyz = np.array(segment)[:,1:4]
        self.ax.scatter(xyz[:,0], xyz[:,1], xyz[:,2], c=c, edgecolors='none')
        self.empty = False
    def for_each_game(self, levelstr):
        if not self.empty:
            self.plot_navpoints()
            self.ax.set_xlabel('X')
            self.ax.set_ylabel('Y')
            self.ax.set_zlabel('Z')
            self.fig.suptitle(self.level)
            plt.savefig(self.level + '.by_navpoint.png', dpi=300)
        else:
            plt.close(self.fig)
        self.empty = True
    def after(self):
        #plt.show()
        pass
        
def main():
    plotter = NavpointPlotter()
    if len(sys.argv) > 1:
        plotter.process(sys.argv[1])
    else:
        plotter.process()

if __name__ == "__main__":
    main()
