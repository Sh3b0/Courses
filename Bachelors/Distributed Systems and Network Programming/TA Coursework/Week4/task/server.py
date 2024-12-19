import sqlite3
from concurrent.futures import ThreadPoolExecutor

import grpc
import schema_pb2 as stub
import schema_pb2_grpc as service

SERVER_ADDR = '0.0.0.0:1234'


class Database(service.DatabaseServicer):
    def PutUser(self, request, context):
        pass

    def GetUsers(self, request, context):
        pass

    def DeleteUser(self, request, context):
        pass


if __name__ == '__main__':
    pass
