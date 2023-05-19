import random

from calculator import count


def test_valid_sum():
    a = random.randint(0, 100000)
    b = random.randint(0, 100000)
    assert count(a, b, '+') == (a+b)


def test_valid_sum_float():
    a = 2.5
    b = 11.32
    assert count(a, b, '+') == (2.5+11.32)


def test_valid_sum_negative_test():
    a = 2.5
    b = -11.32
    assert count(a, b, '+') == (2.5+(-11.32))


def test_valid_sub():
    a = random.randint(0, 100000)
    b = random.randint(0, 100000)
    assert count(a, b, '-') == (a-b)


def test_valid_sub_float():
    a = 7.3
    b = 2.1
    assert count(a, b, '-') == (7.3-2.1)


def test_valid_sub_negative_float():
    a = 7.3
    b = 11.1
    assert count(a, b, '-') == (7.3-11.1)


def test_valid_div():
    a = random.randint(0, 100000)
    b = random.randint(0, 100000)
    assert count(a, b, '/') == (a/b)


def test_valid_div_float():
    a = 22
    b = 33
    assert count(a, b, '/') == (22/33)


def test_valid_div_negative_float():
    a = -22
    b = 33
    assert count(a, b, '/') == (-22/33)
    assert count(b, a, '/') == (33/-22)


def test_valid_mult():
    a = random.randint(0, 100000)
    b = random.randint(0, 100000)
    assert count(a, b, '*') == (a*b)


def test_valid_mult_float():
    a = 0.5
    b = 0.7
    assert count(a, b, '*') == (0.5*0.7)


def test_valid_mult_negative_float():
    a = -0.5
    b = 0.7
    assert count(a, b, '*') == (-0.5*0.7)


def test_div_by_zero():
    a = random.randint(0, 100000)
    b = 0
    assert count(a, b, '/') == "Division by zero!"


def test_invalid_ops():
    a = 1
    b = 2
    for i in [41, 44, 46, 48]:  # values around the valid operators
        assert count(a, b, chr(i)) == "Invalid operator!"
