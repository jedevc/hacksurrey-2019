import solid

from .stump import stump

import random
import hashlib

from . import permute

def main():
    stuff = hashlib.sha256(b'data').digest()
    final = create_model(stuff, 3, 9)

    print(render_model(final))

def create_model(data, rows, columns):
    hexes = to_base(int.from_bytes(data, 'big'), 720)
    assert len(hexes) == rows * columns

    sts = []
    for row in range(rows):
        for col in range(columns):
            order = permute.permute([1, 2, 3, 4, 5, 6], hexes[row * 9 + col])

            heights = [1 + 1.5 * i / len(order) for i in order]
            st = stump(heights)

            xdiff = 1.5 * col
            xdiff *= 1.2
            ydiff = (-3 ** 0.5) * row - (3 ** 0.5 / 2) * col
            ydiff *= 1.2
            st = solid.translate([xdiff, ydiff, 0])(st)

            sts.append(st)

    base = solid.translate([-3.4, -3.2, 0])(
        solid.rotate([0, 0, -30])(
            solid.scale([22, 6.5, 0.3])(
                solid.cube(1)
            )
        )
    )

    x, y = 0.5, (3 ** 0.5 / 2) / 2
    marker = solid.translate([-1.5, -2.5, 0])(
        solid.polyhedron(
            points = [
                # bottom
                (-x, -y, 0), (x, -y, 0), (0, y, 0),
                # top
                (-x, -y, 1), (x, -y, 1), (0, y, 1)
            ],
            faces = [
                # bottom
                (0, 1, 2),
                # top
                (5, 4, 3),
                # sides
                (0, 3, 4, 1), (1, 4, 5, 2), (2, 5, 3, 0)
            ]
        )
    )

    return solid.union()(
        [
            base,
            solid.union()(sts),
            marker
        ]
    )

def render_model(model):
    return solid.scad_render(model)

def to_base(number, base):
    top = 1
    while base ** top <= number:
        top += 1

    parts = []
    for i in range(top - 1, -1, -1):
        value = base ** i
        parts.append(number // value)
        number %= value

    return parts

def from_base(parts, base):
    number = 0

    place = 1
    for value in reversed(parts):
        number += value * place
        place *= base

    return number
