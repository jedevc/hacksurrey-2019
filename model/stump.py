from solid import *
from solid.utils import *

class Stump:
    def __init__(self, heights):
        assert len(heights) == 6

        unit = 1
        x = unit / 2
        y = unit * (3 ** 0.5 / 2) / 2

        tri = polyhedron(
            points = [
                # bottom
                (-x, y, 0),
                (x, y, 0),
                (0, -y, 0),
                # top
                (-x, y, 1),
                (x, y, 1),
                (0, -y, 1)
            ],
            faces = [
                # bottom
                (0, 1, 2),
                # top
                (3, 4, 5),
                # sides
                (0, 1, 4, 3),
                (1, 2, 5, 4),
                (2, 0, 3, 5)
            ]
        )

        self.triangles = [
            tri,
            scale([1, 1, heights[0]])(
                tri
            ),
            scale([1, 1, heights[1]])(
                translate([0, 2 * y, 0])(
                    rotate([0, 0, 180])(
                        tri
                    )
                )
            ),
            scale([1, 1, heights[2]])(
                translate([x, 2 * y, 0])(
                    tri
                )
            ),
            scale([1, 1, heights[3]])(
                translate([2 * x, 2 * y, 0])(
                    rotate([0, 0, 180])(
                        tri
                    )
                )
            ),
            scale([1, 1, heights[4]])(
                translate([2 * x, 0, 0])(
                    tri
                )
            ),
            scale([1, 1, heights[5]])(
                translate([x, 0, 0])(
                    rotate([0, 0, 180])(
                        tri
                    )
                )
            ),
        ]

    def generate(self):
        total = self.triangles[0]
        for triangle in self.triangles[1:]:
            total += triangle
        return total
