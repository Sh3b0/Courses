import requests

API_KEY = '<KEY>'
MY_EMAIL = '<EMAIL>'
    
# Values extracted by querying the script with my email
budget_car_price_per_minute = 24
luxury_car_price_per_minute = 58
fixed_price_per_km = 13
allowed_deviations_in_percent = 11
inno_discount_in_percent = 8

test_no = 0
passed_tests = 0

def expected_price(params):
    """Returns the correct price for given parameters"""
    if not params['type'] in ['budget', 'luxury'] \
            or not params['plan'] in ['minute', 'fixed_price'] \
            or params['distance'] < 0 \
            or params['planned_distance'] < 0 \
            or params['time'] < 0 \
            or params['planned_time'] < 0 \
            or params['distance'] > 100000 \
            or params['planned_distance'] > 100000 \
            or params['time'] > 100000 \
            or params['planned_time'] > 100000 \
            or not params['inno_discount'] in ['yes', 'no'] \
            or (params['type'] == 'luxury' and params['plan'] == 'fixed_price'):
        return "Invalid Request"

    if params['plan'] == 'fixed_price':
        deviation_distance_in_percent = (
            params['planned_distance'] - params['distance']) / params['planned_distance'] * 100
        deviation_time_in_percent = (
            params['planned_time'] - params['time']) / params['planned_time'] * 100
        if deviation_distance_in_percent > allowed_deviations_in_percent or deviation_time_in_percent > allowed_deviations_in_percent:
            params['plan'] = 'minute'
        else:
            price = params['planned_distance'] * fixed_price_per_km
    if params['plan'] == 'minute':
        if params['type'] == 'budget':
            price = params['time'] * budget_car_price_per_minute
        elif params['type'] == 'luxury':
            price = params['time'] * luxury_car_price_per_minute

    if params['inno_discount'] == 'yes':
        return price * (1 - inno_discount_in_percent / 100)
    elif params['inno_discount'] == 'no':
        return price


def actual_price(params):
    """Queries InnoDrive for a price with given parameters"""
    URL = f'https://script.google.com/macros/s/{API_KEY}' + \
        f'/exec?service=calculatePrice&email={MY_EMAIL}&type={params["type"]}' + \
        f'&plan={params["plan"]}&distance={params["distance"]}&planned_distance={params["planned_distance"]}' + \
        f'&time={params["time"]}&planned_time={params["planned_time"]}&inno_discount={params["inno_discount"]}'

    response = requests.get(URL)
    if response.content.decode() == 'Invalid Request':
        return 'Invalid Request'
    return response.json()["price"]


def run_test(params):
    """Runs a test case with given parameters"""
    global test_no, passed_tests
    test_no += 1
    actual = actual_price(params) 
    expected = expected_price(params)
    if expected != actual:
        print(f'{str(test_no).zfill(2)}\tFailed\t{"{:^20}".format(expected)}\t{"{:^20}".format(actual)}\t{list(params.values())}')
        return
    print(f'{str(test_no).zfill(2)}\tPassed\t{"{:^20}".format(expected)}\t{"{:^20}".format(actual)}\t{list(params.values())}')
    passed_tests += 1

print("No.\tVerdict\t\tExpected\t\tGot\t\tParams")

# Run invalid tests (invalidating one parameter at a time while keeping others normal)
run_test({'type': 'spaceship', 'plan': 'minute', 'distance': 1,  'planned_distance': 1,  'time': 1,  'planned_time': 1,  'inno_discount': 'yes'})
run_test({'type': 'budget',    'plan': 'joker',  'distance': 1,  'planned_distance': 1,  'time': 1,  'planned_time': 1,  'inno_discount': 'yes'})
run_test({'type': 'budget',    'plan': 'minute', 'distance': 1,  'planned_distance': 1,  'time': 1,  'planned_time': 1,  'inno_discount': 'why_not'})
run_test({'type': 'budget',    'plan': 'minute', 'distance': -1, 'planned_distance': 1,  'time': 1,  'planned_time': 1,  'inno_discount': 'yes'})
run_test({'type': 'budget',    'plan': 'minute', 'distance': 100001, 'planned_distance': 1,  'time': 1,  'planned_time': 1,  'inno_discount': 'yes'})
run_test({'type': 'budget',    'plan': 'minute', 'distance': 1,  'planned_distance': -1, 'time': 1,  'planned_time': 1,  'inno_discount': 'yes'})
run_test({'type': 'budget',    'plan': 'minute', 'distance': 1,  'planned_distance': 100001, 'time': 1,  'planned_time': 1,  'inno_discount': 'yes'})
run_test({'type': 'budget',    'plan': 'minute', 'distance': 1,  'planned_distance': 1,  'time': -1, 'planned_time': 1,  'inno_discount': 'yes'})
run_test({'type': 'budget',    'plan': 'minute', 'distance': 1,  'planned_distance': 1,  'time': 100001, 'planned_time': 1,  'inno_discount': 'yes'})
run_test({'type': 'budget',    'plan': 'minute', 'distance': 1,  'planned_distance': 1,  'time': 1,  'planned_time': -1, 'inno_discount': 'yes'})
run_test({'type': 'budget',    'plan': 'minute', 'distance': 1,  'planned_distance': 1,  'time': 1,  'planned_time': 100001, 'inno_discount': 'yes'})

# Run boundary tests (extreming one parameter at a time while keeping others normal)
run_test({'type': 'budget',    'plan': 'minute', 'distance': 0, 'planned_distance': 1,  'time': 1,  'planned_time': 1,  'inno_discount': 'yes'})
run_test({'type': 'budget',    'plan': 'minute', 'distance': 100000, 'planned_distance': 1,  'time': 1,  'planned_time': 1,  'inno_discount': 'yes'})
run_test({'type': 'budget',    'plan': 'minute', 'distance': 1,  'planned_distance': 0, 'time': 1,  'planned_time': 1,  'inno_discount': 'yes'})
run_test({'type': 'budget',    'plan': 'minute', 'distance': 1,  'planned_distance': 100000, 'time': 1,  'planned_time': 1,  'inno_discount': 'yes'})
run_test({'type': 'budget',    'plan': 'minute', 'distance': 1,  'planned_distance': 1,  'time': 0, 'planned_time': 1,  'inno_discount': 'yes'})
run_test({'type': 'budget',    'plan': 'minute', 'distance': 1,  'planned_distance': 1,  'time': 100000, 'planned_time': 1,  'inno_discount': 'yes'})
run_test({'type': 'budget',    'plan': 'minute', 'distance': 1,  'planned_distance': 1,  'time': 1,  'planned_time': 0, 'inno_discount': 'yes'})
run_test({'type': 'budget',    'plan': 'minute', 'distance': 1,  'planned_distance': 1,  'time': 1,  'planned_time': 100000, 'inno_discount': 'yes'})

# Regular tests (all valid normal values)
for type in ['budget', 'luxury']:
    for plan in ['minute', 'fixed_price']:
        for discount in ['yes', 'no']:
            run_test({'type': type, 'plan': plan, 'distance': 100, 'planned_distance': 100,
                      'time': 100,  'planned_time': 100, 'inno_discount': discount})

print(f"Ran {test_no} tests. Passed: {passed_tests}. Failed: {test_no - passed_tests}")
