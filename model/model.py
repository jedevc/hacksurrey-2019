from solid import *

from .stump import stump

import random

def main():
    order = [1, 2, 3, 4, 5, 6]
    random.shuffle(order)
    heights = [0.5 + 1.5 * i / len(order) for i in order]
    st = stump(heights)

    scad = scad_render(st)
    print(scad)
