from concurrent.futures import ThreadPoolExecutor

import grpc
import greeter_pb2 as stub
import greeter_pb2_grpc as service


class Greeter(service.GreeterServicer):
    def SayHello(self, request, context):
        response = stub.HelloResponse()
        response.message = f"Hello, {request.name}!"
        return response


if __name__ == '__main__':
    server = grpc.server(ThreadPoolExecutor(max_workers=10))
    service.add_GreeterServicer_to_server(Greeter(), server)
    server.add_insecure_port('0.0.0.0:1234')
    server.start()
    try:
        server.wait_for_termination()
    except KeyboardInterrupt:
        server.stop(0)
        print("Shutting down...")
