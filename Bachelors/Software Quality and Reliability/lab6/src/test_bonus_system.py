from bonus_system import calculateBonuses


def test_standard():
    assert calculateBonuses("Standard", 10000-1) == 0.05*1
    assert calculateBonuses("Standard", 10000) == 0.05*1.5
    assert calculateBonuses("Standard", 50000-1) == 0.05*1.5
    assert calculateBonuses("Standard", 50000) == 0.05*2
    assert calculateBonuses("Standard", 100000-1) == 0.05*2
    assert calculateBonuses("Standard", 100000) == 0.05*2.5
    assert calculateBonuses("Standard", 100000+1) == 0.05*2.5


def test_premium():
    assert calculateBonuses("Premium", 10000-1) == 0.1*1
    assert calculateBonuses("Premium", 10000) == 0.1*1.5
    assert calculateBonuses("Premium", 50000-1) == 0.1*1.5
    assert calculateBonuses("Premium", 50000) == 0.1*2
    assert calculateBonuses("Premium", 100000-1) == 0.1*2
    assert calculateBonuses("Premium", 100000) == 0.1*2.5
    assert calculateBonuses("Premium", 100000+1) == 0.1*2.5


def test_diamond():
    assert calculateBonuses("Diamond", 10000-1) == 0.2*1
    assert calculateBonuses("Diamond", 10000) == 0.2*1.5
    assert calculateBonuses("Diamond", 50000-1) == 0.2*1.5
    assert calculateBonuses("Diamond", 50000) == 0.2*2
    assert calculateBonuses("Diamond", 100000-1) == 0.2*2
    assert calculateBonuses("Diamond", 100000) == 0.2*2.5
    assert calculateBonuses("Diamond", 100000+1) == 0.2*2.5


def test_invalid_type():
    assert calculateBonuses("Standarc", 1) == 0
    assert calculateBonuses("Standare", 1) == 0
    assert calculateBonuses("Premiul", 1) == 0
    assert calculateBonuses("Premiun", 1) == 0
    assert calculateBonuses("Diamonc", 1) == 0
    assert calculateBonuses("Diamone", 1) == 0
