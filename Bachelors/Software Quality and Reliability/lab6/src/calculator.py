def sum(num1, num2):
    return float(num1) + float(num2)


def div(num1, num2):
    return num1 / num2


def mult(num1, num2):
    return num1 * num2


def sub(num1, num2):
    return num1 - num2


def count(num1, num2, sign):
    if sign == "+":
        res = sum(num1, num2)
    elif sign == "-":
        res = sub(num1, num2)
    elif sign == "*":
        res = mult(num1, num2)
    elif sign == "/":
        if num2 == 0:
            res = "Division by zero!"
        else:
            res = div(num1, num2)
    else:
        res = "Invalid operator!"
    return res
