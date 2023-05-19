import http.client
import json
import time

BASE_URL = 'api.chucknorris.io'
ENDPOINT = '/jokes/random'
ntimes = 100


def fetch_jokes():
    jokes = []
    conn = http.client.HTTPSConnection(BASE_URL)
    for _ in range(ntimes):
        conn.request('GET', ENDPOINT)
        jokes.append(json.loads(conn.getresponse().read().decode())["value"])
    conn.close()
    print(jokes)


if __name__ == '__main__':
    t0 = time.time()

    fetch_jokes()

    print(f'Fetching {ntimes} jokes takes {time.time() - t0} seconds')
