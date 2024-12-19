from flask import Flask, request, jsonify, render_template
from xmlrpc.client import ServerProxy
import socket
from multiprocessing import Process
from time import sleep
from node import Node
import logging

log = logging.getLogger('werkzeug')
log.setLevel(logging.ERROR)
app = Flask(__name__)

ports = {}
nodes = {}
m = 0


def get_free_port():
    sock = socket.socket()
    sock.bind(('', 0))
    return sock.getsockname()[1]


@app.route('/')
def index():
    return render_template('index.html')


@app.route('/api/put', methods=['POST'])
def api_put():
    data = request.json
    n, k = int(data['n']), int(data['k'])
    v = data['v']

    if n not in ports:
        return jsonify({"error": "Invalid node ID"}), 400
    try:
        with ServerProxy(f'http://localhost:{ports[n]}') as node:
            result = node.put(k, v)
        return jsonify({"result": result})
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route('/api/get', methods=['GET'])
def api_get():
    n = request.args.get('n', type=int)
    k = request.args.get('k', type=int)
    if n not in ports:
        return jsonify({"error": "Invalid node ID"}), 400

    try:
        with ServerProxy(f'http://localhost:{ports[n]}') as node:
            result = node.get(k)
        return jsonify({"result": result})
    except Exception as e:
        return jsonify({"error": str(e)}), 500


if __name__ == '__main__':
    print("Client started")

    # Input configuration
    m = int(input("M: "))
    ring = list(map(int, input("RING: ").split(' ')))

    for node_id in ring:
        ports[node_id] = get_free_port()

    for node_id in ring:
        nodes[node_id] = Process(target=Node, args=(node_id, m, ring, ports))
        nodes[node_id].start()

    sleep(1)  # Await node initializations

    try:
        app.run()
    except KeyboardInterrupt:
        for p in nodes.values():
            p.join()
