#!/usr/bin/env python

"""
plot navpoints (needs Levels and level-counts.txt)
"""

import numpy as np
from mpl_toolkits.mplot3d import axes3d, Axes3D
import matplotlib.pyplot as plt
from matplotlib import cm
import sys
from utils import PoseVisitor, get_level
import sqlite3

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
        
class NavpointPlotter(PoseVisitor):
    def before(self, levelstr):
        self.level = get_level(levelstr)
        self.fig = plt.figure()
        self.ax = Axes3D(self.fig)
        self.ax.hold(True)
        self.empty = True
    def plot_navgraph(self):
        label = 'NAVGRAPH'
        for link in self.links:
            (x0, y0, z0) = self.navpoints[link[0]][1]
            (x1, y1, z1) = self.navpoints[link[1]][1]
            self.ax.plot( [x0, x1], [y0, y1], [z0, z1], label=label, color='black', linestyle=':', marker='None' )
            label = '_nolabel_'
    def plot_navpoints(self):
        counts = np.array(self.counts.values())
        max_paths = np.max(counts[:,0])
        max_points = np.max(counts[:,1])
        cmap = cm.get_cmap('hot')
        label = 'NAVPOINT'
        empty_navpoints = 0
        for npid in self.navpoints:
            name, (x,y,z) = self.navpoints[npid]
            paths, points = self.counts[npid]
            paths, points = float(paths), float(points)
            if points == 0:
                s = 15
                empty_navpoints += 1
                c = (1,1,1)
            else:
                s = 10*paths/max_paths
                c = (1-points/max_points, 1-points/max_points, 1-points/max_points)
            self.ax.plot( [x],[y],[z], markerfacecolor=c, markersize=s, label=label, linestyle='None', marker='s' )
            label='_nolabel_'
        print '%s: %d/%d' % (self.level, empty_navpoints, len(self.navpoints))
    def for_each_segment(self, segment, label, color):
        if not label:
            label = '_nolegend_' # same player as before, use the same label
        self.empty = False
    def for_each_game(self, levelstr):
        if not self.empty:
            self.ax.set_xlabel('X')
            self.ax.set_ylabel('Y')
            self.ax.set_zlabel('Z')
            (self.navpoints, self.links, self.counts) = get_navpoints(self.level)
            self.plot_navgraph()
            self.plot_navpoints()
            self.ax.legend()
            self.fig.suptitle(self.level)
            plt.savefig(self.level + '.navpoints.png', dpi=300)
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
