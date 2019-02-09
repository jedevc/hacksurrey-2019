from solid import *

from .stump import stump

import random

from . import permute

def main():
    stuff = bytes(random.randint(0, 255) for i in range(32))
    stuff = int.from_bytes(stuff, 'big')
    hexes = to_base(stuff, 720)

    assert len(hexes) == 27

    for row in range(3):
        for col in range(9):
            order = permute.permute([1, 2, 3, 4, 5, 6], hexes[row * 9 + col])

            heights = [0.5 + 1.5 * i / len(order) for i in order]
            st = stump(heights)

            xdiff = 1.5 * col
            xdiff *= 1.1
            ydiff = (-3 ** 0.5) * row - (3 ** 0.5 / 2) * col
            ydiff *= 1.1
            st = translate([xdiff, ydiff, 0])(st)

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
