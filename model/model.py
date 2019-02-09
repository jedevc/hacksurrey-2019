from solid import *

from .stump import stump

import random

def main():
    order = [1, 2, 3, 4, 5, 6]
    random.shuffle(order)
    st = stump(order)

    scad = scad_render(st)
    print(scad)

