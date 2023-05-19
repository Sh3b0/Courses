import asyncio
import aiohttp
import time

BASE_URL = 'https://api.chucknorris.io'
ENDPOINT = '/jokes/random'
ntimes = 100


async def fetch_joke(session):
    async with session.get(BASE_URL + ENDPOINT) as response:
        return await response.json()


async def fetch_jokes():
    async with aiohttp.ClientSession() as session:
        jokes = await asyncio.gather(*[fetch_joke(session) for _ in range(ntimes)])
    print([joke['value'] for joke in jokes])


if __name__ == '__main__':
    t0 = time.time()

    asyncio.run(fetch_jokes())

    print(f'Fetching {ntimes} jokes takes {time.time() - t0} seconds')
