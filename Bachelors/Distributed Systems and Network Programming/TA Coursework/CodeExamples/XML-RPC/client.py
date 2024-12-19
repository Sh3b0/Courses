from xmlrpc.client import ServerProxy

if __name__ == '__main__':
    with ServerProxy('http://127.0.0.1:1234') as rpc:
        print(rpc.method(123, "some_string"))
        # print(rpc.property)     # not what you expect
        # print(rpc.procedure())  # not allowed unless allow-none=True is specified
