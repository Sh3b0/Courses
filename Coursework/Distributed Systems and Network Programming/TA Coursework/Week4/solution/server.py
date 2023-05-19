import sqlite3
from concurrent.futures import ThreadPoolExecutor

import grpc
import schema_pb2 as stub
import schema_pb2_grpc as service

SERVER_ADDR = '0.0.0.0:1234'


def init_db():
    """Creates or overwrites the database file, then initializes the db with an empty `Users` table"""
    open('db.sqlite', 'w').close()
    try:
        conn = sqlite3.connect('db.sqlite')
        cur = conn.cursor()
        cur.execute(
            'CREATE TABLE Users (Id INTEGER NOT NULL PRIMARY KEY, Name STRING)')
        conn.commit()
    except sqlite3.Error as error:
        print(f"Failed to initialize the database: {error}")
        return
    conn.close()


class Database(service.DatabaseServicer):
    def connect(self):
        """Returns a Connection and a Cursor object to the SQLite database"""
        try:
            conn = sqlite3.connect('db.sqlite')
            cur = conn.cursor()
        except sqlite3.Error as error:
            print(f"Failed to connect to the database: {error}")
            return
        return conn, cur

    def PutUser(self, request, context):
        print(f"PutUser({request.user_id}, '{request.user_name}')")
        conn, cur = self.connect()
        cur.execute('INSERT OR REPLACE INTO Users (Id, Name) VALUES (?, ?)',
                    [request.user_id, request.user_name])
        conn.commit()
        conn.close()
        return stub.BooleanResponse(status=True)

    def GetUsers(self, request, context):
        print(f"GetUsers()")
        conn, cur = self.connect()
        cur.execute('SELECT * FROM Users')
        conn.commit()
        results = cur.fetchall()
        conn.close()
        return stub.UserList(users=[stub.User(user_name=user_name, user_id=user_id)
                                    for user_id, user_name in results])

    def DeleteUser(self, request, context):
        print(f"DeleteUser({request.user_id})")
        conn, cur = self.connect()
        cur.execute('DELETE FROM Users WHERE Id = (?)', [request.user_id])
        conn.commit()
        conn.close()
        return stub.BooleanResponse(status=True)


if __name__ == '__main__':
    init_db()
    server = grpc.server(ThreadPoolExecutor(max_workers=10))
    service.add_DatabaseServicer_to_server(Database(), server)
    server.add_insecure_port(SERVER_ADDR)
    server.start()
    print(f"gRPC server is listening on {SERVER_ADDR}")
    try:
        server.wait_for_termination()
    except KeyboardInterrupt:
        server.stop(0)
        print("Shutting down...")
