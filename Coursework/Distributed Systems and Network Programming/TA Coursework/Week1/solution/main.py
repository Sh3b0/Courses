import argparse
import os
import socket

sessions = {}
BUF_SIZE = 20480


class Session:
    def __init__(self, file_name, file_size):
        self.file_name = file_name
        self.file_size = file_size
        self.expected_seqno = 1
        self.bytes_received = 0
        self.chunk_no = 1


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("server_port", type=int)
    args = parser.parse_args()

    with socket.socket(family=socket.AF_INET, type=socket.SOCK_DGRAM) as s:
        s.settimeout(1)
        s.bind(("0.0.0.0", args.server_port))
        print(f"{s.getsockname()}:\tListening...")
        while True:

            # Wait for a client message
            try:
                data, addr = s.recvfrom(BUF_SIZE)
            except KeyboardInterrupt:
                print(f"{s.getsockname()}:\tShutting down...")
                break
            except socket.timeout:
                continue

            packet_type = data[:1].decode()

            # Handle start messages
            if packet_type == "s":
                print(f"{addr}:\t{data.decode()}")
                _, seqno, file_name, file_size = data.decode().split("|")
                if os.path.exists(file_name):
                    print(f"{s.getsockname()}:\tOverwriting {file_name}...")
                    os.remove(file_name)

                sessions[addr] = Session(file_name, int(file_size))
                s.sendto(f"a|{(int(seqno) + 1) % 2}".encode(), addr)

            # Handle data messages
            elif packet_type == "d":
                seqno = int(data[2:3])
                if sessions.get(addr):
                    session = sessions[addr]
                else:
                    print(f"{s.getsockname()}:\tUnexpected data message received. Ignoring...")
                    continue

                if seqno != session.expected_seqno:
                    print(f"{s.getsockname()}:\tRetransmitting ACK...")
                    s.sendto(f"a|{(seqno + 1) % 2}".encode(), addr)
                    continue

                print(f"{addr}:\t{data[:4].decode()}chunk{session.chunk_no}")

                with open(session.file_name, "ab") as f:
                    f.write(data[4:])
                    session.bytes_received += len(data[4:])
                    if session.bytes_received == session.file_size:
                        print(f"{s.getsockname()}:\tReceived {session.file_name}.")

                session.chunk_no += 1
                session.expected_seqno = (seqno + 1) % 2

                # Reply with ACK
                s.sendto(f"a|{(seqno + 1) % 2}".encode(), addr)

            # Handle unknown messages
            else:
                print(f"{s.getsockname()}:\tUnknown packet type {packet_type}. Aborting...")
                exit()
