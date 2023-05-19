import argparse
import math
import os
import socket

MSS = 20476  # MSS = Server buffer size (20480) - data header size (4)


def await_ack(packet):
    s.settimeout(1)
    while True:
        try:
            data, addr = s.recvfrom(1024)
            print(f"Server: {data.decode()}")
            received_ack_seqno = int(data[2:3].decode())
            expected_ack_seqno = (int(packet[2:3].decode()) + 1) % 2
            if received_ack_seqno != expected_ack_seqno:
                continue
            return True
        except KeyboardInterrupt:
            print("Client: Exiting...")
            exit()
        except socket.timeout:  # Expected ACK was not received within one second
            print(f"Client: Retransmitting...")
            s.sendto(packet, (server_ip, server_port))


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("server_addr", type=str)
    parser.add_argument("file_path", type=str)
    args = parser.parse_args()

    server_ip, server_port = args.server_addr.split(":")
    server_port = int(server_port)
    file_name = args.file_path.split(os.path.sep)[-1]
    file_size = os.path.getsize(args.file_path)

    with socket.socket(family=socket.AF_INET, type=socket.SOCK_DGRAM) as s:
        s.setsockopt(socket.SOL_SOCKET, socket.SO_SNDBUF, 30000)

        # Check file existence
        if not os.path.exists(args.file_path):
            print(f"Client: no such file: {args.file_path}")
            exit()

        # Send start packet to server
        packet = f"s|0|{file_name}|{file_size}".encode()
        print(f"Client: {packet.decode()}")
        s.sendto(packet, (server_ip, server_port))

        # Wait for ACK for the given packet
        await_ack(packet)

        # Upload file to server
        seqno = 1
        with open(args.file_path, "rb") as f:
            for i in range(math.ceil(file_size / MSS)):
                print(f"Client: d|{seqno}|chunk{i + 1}")
                packet = bytes(f"d|{seqno}|", "utf-8") + f.read(MSS)
                s.sendto(packet, (server_ip, server_port))
                await_ack(packet)
                seqno = (seqno + 1) % 2
