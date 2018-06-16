import os
import os.path
from glob import glob
from math import sqrt
import itertools

THRESHOLD_T = 10.0 # delta time to interrupt (seconds) - could be due to standing
THRESHOLD_D = 100.0 # delta distance to interrupt (game distance units)
THRESHOLD_N = 10 # minimal length of segment to include in output
COLORS = itertools.cycle(['b', 'g', 'r', 'c', 'm', 'y', 'k'])

class PoseVisitor:
    def __init__(self, threshold_time = THRESHOLD_T, threshold_distance = THRESHOLD_D, threshold_length = THRESHOLD_N):
        self.threshold_time = threshold_time
        self.threshold_distance = threshold_distance
        self.threshold_length = threshold_length
    def before(self, level):
        assert(False)
    def for_each_segment(self, segment, label, color):
        assert(False)
    def for_each_game(self, level):
        assert(False)
    def after(self):
        assert(False)
    def process(self, level = None):
        dirnames = []
        if level is None:
            dirnames = glob(os.path.join('Levels','DM-*'))
            dirnames = [dirname for dirname in dirnames if os.path.isdir(dirname)]
        else:
            dirnames = [level]
        for dirname in dirnames:
            files = glob(os.path.join(dirname, '*', '*.dat'))
            self.before(dirname)
            for fname in files:
                segment = []
                times = []
                with open(fname) as f:
                    label = os.path.basename(fname)[0:-4]
                    color = COLORS.next()
                    skip_time = 0
                    for l in f:
                        numbers = [float(x) for x in l.strip().split()]
                        t, x, y, z, rx, ry, rz = numbers[0:7]
                        if segment:
                            prev = segment[-1]
                            numbers0 = prev[0:7]
                            t0, x0, y0, z0, rx0, ry0, rz0 = numbers0
                            d = sqrt( (x - x0) ** 2 + (y - y0) ** 2 + (z - z0) ** 2 )
                            if (t - t0) > self.threshold_time or d > self.threshold_distance:
                                if len(segment) > self.threshold_length:
                                    self.for_each_segment(segment, label, color)
                                    skip_time = 0 # reset skip time
                                    label = None
                                segment = [numbers]
                            else:
                                if x!=x0 or y!=y0 or z!=z0:
                                    # if some movement was done, append
                                    segment.append(tuple([t - skip_time] + numbers[1:]))
                                else:
                                    # otherwise, don't append but also skip the time
                                    skip_time = (t - t0)                                    
                        else:
                            segment.append( numbers )
                if len(segment) > self.threshold_length:
                    self.for_each_segment(segment, label, color)
            self.for_each_game(dirname)
        self.after()

def get_level(levelstr):
    if levelstr.startswith('Levels'):
        return levelstr[7:]
    else:
        return levelstr

        
class IndexedPoseVisitor:
    def __init__(self):
        pass
    def before(self, level):
        assert(False)
    def for_each_segment(self, segment, label, color):
        assert(False)
    def for_each_game(self, level):
        assert(False)
    def after(self):
        assert(False)
    def process(self, level = None):
        dirnames = []
        if level is None:
            dirnames = glob(os.path.join('Levels','DM-*'))
            dirnames = [dirname for dirname in dirnames if os.path.isdir(dirname)]
        else:
            dirnames = [level]
        for dirname in dirnames:
            files = glob(os.path.join(dirname, '*.indexed.dat'))
            self.before(dirname)
            for fname in files:
                segment = []
                times = []
                with open(fname) as f:
                    label = os.path.basename(fname)[0:-4]
                    color = COLORS.next()
                    skip_time = 0
                    for l in f:
                        l = l.strip().split()
                        segment.append( [float(x) for x in l[0:7]] + [ int(l[-1]) ] )
                if len(segment) > 0:
                    self.for_each_segment(segment, label, color)
            self.for_each_game(dirname)
        self.after()