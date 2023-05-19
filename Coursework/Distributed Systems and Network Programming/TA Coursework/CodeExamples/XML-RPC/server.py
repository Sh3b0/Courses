from xmlrpc.server import SimpleXMLRPCServer


class RemoteFunctions():
    def __init__(self):
        self.propery = "value"

    def method(self, int_param, str_param):
        return True

    def procedure(self):
        return


if __name__ == '__main__':
    with SimpleXMLRPCServer(('0.0.0.0', 1234), logRequests=False) as server:
        server.register_introspection_functions()
        server.register_instance(RemoteFunctions())
        try:
            print("RPC server is listening at http://0.0.0.0:1234")
            server.serve_forever()
        except KeyboardInterrupt:
            print("Shutting down...")
