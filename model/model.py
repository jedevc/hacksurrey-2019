from solid import *

from .stump import stump

import random

from . import permute

def main():
    stuff = bytes(random.randint(0, 255) for i in range(32))
    stuff = int.from_bytes(stuff, 'big')
    hexes = to_base(stuff, 720)

    order = permute.permute([1, 2, 3, 4, 5, 6], hexes[0])

    heights = [0.5 + 1.5 * i / len(order) for i in order]
    st = stump(heights)

    scad = scad_render(st)
    print(scad)

def to_base(number, base):
    top = 1
    while base ** top < number:
        top += 1

    parts = []
    for i in range(top - 1, -1, -1):
        value = base ** i
        parts.append(number // value)
        number %= value

    return parts
