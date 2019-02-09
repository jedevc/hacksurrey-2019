from solid import *

from .stump import stump

import random

from . import permute

def main():
    order = permute.permute([1, 2, 3, 4, 5, 6], random.randint(0, 719))

    heights = [0.5 + 1.5 * i / len(order) for i in order]
    st = stump(heights)

    scad = scad_render(st)
    print(scad)
