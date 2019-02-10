import math

def permute(ordered, number):
    li = ordered[:]
    final = []
    
    seq = to_factoradic(number, len(li))
    for i in seq:
        final.append(li.pop(i))

    return final

def depermute(permuted, ordered):
    li = ordered[:]
    seq = []

    for i in permuted:
        index = li.index(i)
        seq.append(index)
        li.pop(index)

    return from_factoradic(seq)

def to_factoradic(number, places):
    data = []

    assert number < math.factorial(places)

    for i in range(places - 1, -1, -1):
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
