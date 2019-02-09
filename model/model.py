from solid import *

from .stump import Stump

import random

def main():
    order = [1, 2, 3, 4, 5, 6]
    random.shuffle(order)
    stump = Stump(order)

    scad = scad_render(stump.generate())
    print(scad)

