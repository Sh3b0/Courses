from xmlrpc.client import ServerProxy

if __name__ == '__main__':
    print('The client starts, available commands:')
    print('\tconnect <address> <port>')
    print('\tgetleader')
    print('\tsuspend <period>')
    print('\tquit')

    server = None
    while True:
        inp = ['', '', '']
        try:
            inp = input("Enter command: ").strip().split(' ')
            if len(inp) == 1:
                if inp[0] == 'quit':
                    print('\nThe client ends')
                    exit()
                elif inp[0] == 'getleader':
                    if server is None:
                        print('Client is not connected to any server.')
                    else:
                        print(server.get_leader())
            elif len(inp) == 2 and inp[0] == 'suspend' and inp[1].isdigit():
                if server is None:
                    print('Client is not connected to any server.')
                else:
                    server.suspend(int(inp[1]))
            elif len(inp) == 3 and inp[0] == 'connect' and inp[2].isdigit():
                server = ServerProxy(f'http://{inp[1]}:{inp[2]}')
                server.ping()
            else:
                print('Invalid command')

        except KeyboardInterrupt:
            print('\nThe client ends')
            exit()
        except ConnectionRefusedError:
            print(f'The server {inp[1]}:{inp[2]} is unavailable.')
