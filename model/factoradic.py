import math

def to_factoradic(number, places):
    data = []

    for i in range(places, -1, -1):
        result = number // math.factorial(i)
        data.append(result)
        if result > 0:
            number %= math.factorial(i)

    return data

def from_factoradic(data):
    number = 0
    for place, value in enumerate(reversed(data)):
        number += value * math.factorial(place)
    return number
