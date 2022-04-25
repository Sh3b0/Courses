import argparse
import os
import pickle
from xmlrpc.server import SimpleXMLRPCServer

STORAGE = os.path.dirname(os.path.abspath(__file__)) + os.path.sep + 'server_storage' + os.path.sep
if not os.path.exists(STORAGE):
    os.mkdir(STORAGE)


def send_file(filename, data):
    if os.path.exists(STORAGE + filename):
        print(filename + " not saved")
        return False, f"File already exists in {STORAGE}"

    with open(STORAGE + filename, 'wb') as f:
        f.write(pickle.loads(data.data))
    print(filename + " saved")
    return True, ""


def list_files():
    return os.listdir(STORAGE)


def delete_file(filename):
    if os.path.exists(STORAGE + filename):
        os.remove(STORAGE + filename)
        print(filename + " deleted")
        return True, ""

    print(filename + " not deleted")
    return False, f"No such file in {STORAGE}"


def get_file(filename):
    if not os.path.exists(STORAGE + filename):
        print(f'No such file in {STORAGE}: {filename}')
        return False, f'No such file in {STORAGE}: {filename}'

    with open(STORAGE + filename, 'rb') as f:
        result = pickle.dumps(f.read())
    print(f'File sent: {filename}')
    return True, result


def calculate(expression):
    [opr, op1, op2] = expression.split(' ')
    try:
        if '.' in op1:
            op1 = float(op1)
        else:
            op1 = int(op1)

        if '.' in op2:
            op2 = float(op2)
        else:
            op2 = int(op2)
    except ValueError:
        print(f'{expression} -- not done')
        return False, 'Wrong expression'

    if opr == '+':
        res = op1 + op2
    elif opr == '-':
        res = op1 - op2
    elif opr == '*':
        res = op1 * op2
    elif opr == '/':
        if op2 != 0:
            res = op1 / op2
        else:
            print(f'{expression} -- not done')
            return False, 'Division by zero'
    elif opr == '>':
        res = op1 > op2
    elif opr == '<':
        res = op1 < op2
    elif opr == '>=':
        res = op1 >= op2
    elif opr == '<=':
        res = op1 <= op2
    else:
        print(f'{expression} -- not done')
        return False, 'Wrong expression'

    print(f'{expression} -- done')
    return True, res


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('ip', type=str)
    parser.add_argument('port', type=int)
    args = parser.parse_args()

    # Create server
    with SimpleXMLRPCServer((args.ip, args.port)) as server:
        print("Server is ready...")
        server.register_introspection_functions()

        server.register_function(send_file)
        server.register_function(list_files)
        server.register_function(delete_file)
        server.register_function(get_file)
        server.register_function(calculate)

        try:
            server.serve_forever()
        except KeyboardInterrupt:
            print('Server is stopping')
            exit()
        except Exception as e:
            print(e)
