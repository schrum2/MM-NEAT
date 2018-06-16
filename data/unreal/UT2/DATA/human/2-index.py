from utils import PoseVisitor
import sqlite3
from pprint import pprint
import os
import numpy as np
from scipy.spatial import KDTree

def get_navpoints(level):
    conn = sqlite3.connect('../navpoints.db')
    c = conn.cursor()
    c.execute('select id, x, y, z from navpoint where level=?', (level,) )
    navpoints = []
    navpointids = []
    for row in c:
        navpoints.append( tuple(row[1:]) )
        navpointids.append( row[0] )
    c.close()
    conn.close()
    return (navpoints, navpointids)

class PoseExporter(PoseVisitor):
    def __init__(self):
        # be more conservative when breaking up segments here (leave it to UT2Bot)
        PoseVisitor.__init__(self)
    def before(self, level):
        # connect to DB
        self.segments = []
    def for_each_segment(self, segment, label, color):
        if label: # new player
            self.player = label
        self.segments.append(segment)
    def for_each_game(self, level):
        # a game was recorded
        lev = os.path.basename(level)
        navpoints, navpointids = get_navpoints(lev)
        navpoints_crossed = set()
        kdnavpoints = KDTree(np.array(navpoints))
        i = 0
        for segment in self.segments:
            p0 = segment[0]
            pF = segment[-1]
            if len(segment) > 2:
                with open(os.path.join(level, 'segment-%d.indexed.dat' % i), 'w') as f:
                    for numbers in segment:
                        t,x,y,z,rx,ry,rz = numbers[0:7]
                        (d,idx) = kdnavpoints.query( (x,y,z) )
                        npid = navpointids[idx]
                        navpoints_crossed.add(npid)
                        print >>f,' '.join([str(x) for x in numbers]),npid
                segment = np.array(segment)
                times = segment[:,0]
                tdiffs = times[1:-1] - times[0:-2]
                pos = segment[:,2:4]
                pdiff = segment[1:-1,2:4] - segment[0:-2,2:4]
                pnorms = np.apply_along_axis(np.linalg.norm, 1, pdiff)
                print '%d/%d: %d over %s t: %.2f %.2f %.2f d: %.2f %.2f %.2f' % (i, len(self.segments), len(segment), str(pF[0]-p0[0]), np.min(tdiffs), np.mean(tdiffs), np.max(tdiffs), np.min(pnorms), np.mean(pnorms), np.max(pnorms))
                i += 1
        print 'Level: %s, %d out of %d navpoints touched' % (lev, len(navpoints_crossed), len(navpoints))
    def after(self):
        # end of recording disconnect from DB
        pass

if __name__ == "__main__":
    exporter = PoseExporter()
    exporter.process()
    #get_navpoints('DM-Antalus')
