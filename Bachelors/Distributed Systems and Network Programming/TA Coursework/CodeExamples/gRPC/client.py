import grpc

import greeter_pb2 as service
import greeter_pb2_grpc as stub

if __name__ == '__main__':
    with grpc.insecure_channel('localhost:1234') as channel:
        request = service.HelloRequest(name='World')
        response = stub.GreeterStub(channel).SayHello(request)
        print(response.message)
