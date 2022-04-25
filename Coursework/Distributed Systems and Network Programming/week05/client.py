import argparse
import os
import pickle
import xmlrpc.client

STORAGE = os.path.dirname(os.path.abspath(__file__)) + os.path.sep + 'client_storage' + os.path.sep
DONE = 'Completed'
ERROR = 'Not completed'
INVALID = 'Not completed\nWrong command'
QUIT = 'Client is stopping'

if not os.path.exists(STORAGE):
    os.mkdir(STORAGE)


def rpc_send_file(filename):
    if not os.path.exists(STORAGE + filename):
        print(f'{ERROR}\nNo such file in {STORAGE}')
        return
    with open(STORAGE + filename, 'rb') as f:
        res, msg = server.send_file(filename, pickle.dumps(f.read()))
        if res:
            print(DONE)
        else:
            print(f'{ERROR}\n{msg}')


def rpc_get_file(old_name, new_name=None):
    if new_name is None or new_name == old_name:
        if os.path.exists(STORAGE + old_name):
            print(f'{ERROR}\nFile already exists in {STORAGE}')
            return
        res, msg = server.get_file(arg)
        if res:
            print(DONE)
            with open(STORAGE + arg) as f:
                f.write(msg)
        else:
            print(f'{ERROR}\n{msg}')
    else:
        # Case 1: Two names exist on client
        if os.path.exists(STORAGE + arg1) and os.path.exists(STORAGE + arg2):
            print(f'{ERROR}\nFiles {arg1}, {arg2} already exist in {STORAGE}')
            return

        # Case 2: old_name doesn't exist, but new_name does
        if os.path.exists(STORAGE + arg2):
            print(f'{ERROR}\nFile {arg2} already exists in {STORAGE}')
            return

        # Case 3: old_name exists but new name doesn't, getting the server version.
        # Case 4: both names doesn't exist in client_storage, asking server.
        else:
            res, msg = server.get_file(arg1)
            if res:
                print(DONE)
                with open(STORAGE + arg2, 'wb') as f:
                    f.write(pickle.loads(msg.data))
            else:
                print(f'{ERROR}\n{msg}')


def rpc_delete_file(filename):
    res, msg = server.delete_file(filename)
    if res:
        print(DONE)
    else:
        print(f'{ERROR}\n{msg}')


def rpc_calculate(opr, op1, op2):
    res, msg = server.calculate(' '.join([opr, op1, op2]))
    if res:
        print(f'{msg}\n{DONE}')
    else:
        print(f'{ERROR}\n{msg}')


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('ip', type=str)
    parser.add_argument('port', type=int)
    inp_count = parser.parse_args()

    server = xmlrpc.client.ServerProxy(f'http://{inp_count.ip}:{inp_count.port}')

    try:
        while True:
            inp = input("\nEnter the command: ").rstrip()
            inp_count = len(inp.split(' '))

            if inp_count == 1:
                if inp == 'list':
                    print(f'{server.list_files()}\n{DONE}')
                elif inp == 'quit':
                    print(QUIT)
                    exit()
                else:
                    print(INVALID)

            elif inp_count == 2:
                cmd, arg = inp.split(' ')
                if cmd == 'send':
                    rpc_send_file(arg)
                elif cmd == 'delete':
                    rpc_delete_file(arg)
                elif cmd == 'get':
                    rpc_get_file(arg)
                else:
                    print(INVALID)

            elif inp_count == 3:
                cmd, arg1, arg2 = inp.split(' ')
                if cmd == 'get':
                    rpc_get_file(arg1, arg2)
                else:
                    print(INVALID)

            elif inp_count == 4:
                cmd, arg1, arg2, arg3 = inp.split(' ')
                if cmd == 'calc':
                    rpc_calculate(arg1, arg2, arg3)
                else:
                    print(INVALID)
            else:
                print(INVALID)

    except KeyboardInterrupt:
        print(QUIT)
        exit()
    except Exception as e:
        print(e)
