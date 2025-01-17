#!/usr/bin/env python

from __future__ import print_function
import sys, random, libmapper as mpr

try:
    import numpy23b as np
except:
    print('this test requires numpy, quitting now')
    quit()

def h(sig, event, id, val, time):
    print('  handler got', sig['name'], '=', type(val), val, 'at time', time.get_double())

src = mpr.Device("py.testnumpy.src")
outsig = src.add_signal(mpr.Direction.OUTGOING, "outsig", 10, mpr.Type.NP_INT32, None, 0, 1)

dest = mpr.Device("py.testnumpy.dst")
insig = dest.add_signal(mpr.Direction.INCOMING, "insig", 10, mpr.Type.NP_FLOAT, None, 0, np.array([1,2,3]), None, h)

print("insig properties:")
print("  type:", insig['type'])
min = insig['min']
print("  min:", type(min), min)
max = insig['max']
print("  min:", type(max), max)

while not src.ready or not dest.ready:
    src.poll(10)
    dest.poll(10)

map = mpr.Map(outsig, insig)
map.push()

while not map.ready:
    src.poll(10)
    dest.poll(10)

for i in range(100):
    outsig.set_value(np.array([i, i+1, i+2, i+3, i+4, i+5, i+6, i+7, i+8, i+9]))
    dest.poll(10)
    src.poll(0)

src.free()
dest.free()

