#!/usr/bin/env python

"""
plot position data from a time, position, rotation file
"""

import numpy as np
from mpl_toolkits.mplot3d import axes3d, Axes3D
import matplotlib.pyplot as plt
import sys
import re

fig = plt.figure()
ax = Axes3D(fig)
ax.hold(True)

# HRC:POINTS: [2448.59; -1015.19; -206.10] [2445.40; -1063.05; -203.92] [2439.18; -1180.01; -203.92] [2433.39; -1298.93; -203.92]
points_re = re.compile(r'HRC:POINTS: \[(.*); (.*); (.*)\] \[(.*);(.*); (.*)\] \[(.*); (.*); (.*)\] \[(.*); (.*); (.*)\]')
good_points = []
bad_points = []
with open('HumanTraceBotOsiris2-good.log') as f:
    for line in f:
        m = points_re.match(line.strip())
        if m:
            good_points.append([float(x) for x in m.groups()])
with open('HumanTraceBotOsiris2-bad.log') as f:
    for line in f:
        m = points_re.match(line.strip())
        if m:
            bad_points.append([float(x) for x in m.groups()])
label = 'good'
good_points = np.array(good_points)
bad_points = np.array(bad_points)
for r in range(np.size(good_points,0)):
    row = good_points[r,3:]
    seg = row.reshape((3,3))
    ax.plot(seg[:,0], seg[:,1], seg[:,2], label=label,color='green')
    label = '_nolabel_'
ax.plot(good_points[:,0], good_points[:,1], good_points[:,2], linestyle='None', marker='x', color='blue', label='good path')
label = 'bad'
for r in range(np.size(bad_points,0)):
    row = bad_points[r,3:]
    seg = row.reshape((3,3))
    ax.plot(seg[:,0], seg[:,1], seg[:,2], label=label,color='red')
    label = '_nolabel_'
ax.plot(bad_points[:,0], bad_points[:,1], bad_points[:,2], linestyle='None', marker='x', color='black', label='bad path')
ax.set_xlabel('X')
ax.set_ylabel('Y')
ax.set_zlabel('Z')
plt.show()
