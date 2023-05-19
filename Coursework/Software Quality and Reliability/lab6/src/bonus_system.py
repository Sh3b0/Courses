def calculateBonuses(program, amount):
    multiplier = 0.05 if program == "Standard" else \
        0.1 if program == "Premium" else \
        0.2 if program == "Diamond" else 0
    bonus = 1 if amount < 10000 else \
        1.5 if amount < 50000 else \
        2 if amount < 100000 else 2.5
    return bonus * multiplier
