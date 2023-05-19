import grpc

import schema_pb2 as service
import schema_pb2_grpc as stub


def put_user(user_id, user_name):
    args = service.User(user_id=user_id, user_name=user_name)
    response = stub.PutUser(args)
    print(f"PutUser({user_id}, '{user_name}') = {response.status}")


def get_users():
    args = service.EmptyMessage()
    response = stub.GetUsers(args)
    result = {}
    for user in response.users:
        result[user.user_id] = user.user_name
    print(f"GetUsers() = {result}")


def delete_user(user_id):
    args = service.User(user_id=user_id)
    response = stub.DeleteUser(args)
    print(f"DeleteUser({user_id}) = {response.status}")


if __name__ == '__main__':
    with grpc.insecure_channel('localhost:1234') as channel:
        stub = stub.DatabaseStub(channel)

        # Create four users
        [put_user(i, f"User{i}") for i in range(1, 5)]

        # Update the usename of the second user
        put_user(2, "User2_updated")

        # Delete the thrid user
        delete_user(3)

        # Retrieve all users
        get_users()
