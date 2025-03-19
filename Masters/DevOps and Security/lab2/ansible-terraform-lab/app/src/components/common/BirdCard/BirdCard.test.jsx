import {render, screen, waitFor} from "@testing-library/react";
import BirdCard from "./BirdCard.jsx";

jest.mock('node-fetch');
import fetch from 'node-fetch';

const {Response} = jest.requireActual('node-fetch');

const birdCompleted = {
    sono: {
        small: 'Abc'
    },
    sp: 'test-specie',
    loc: 'test-location',
    en: 'test-en'
};

const mockFetchReturnValue = {
    parse: {
        images: [
            'test-en 1234.jpg'
        ]
    },
    query: {
        pages: [
            {
                'imageinfo': [
                    {
                        'url': 'http://test-url'
                    }
                ]
            }
        ]
    }
};

const birdNotCompleted = {
    sono: {
        small: 'Abc'
    }
};

describe('BirdCard tests', () => {

    afterEach(() => {
        // Delete mocked fetch
        global.fetch.mockClear();
        delete global.fetch;
    });

    test('Passing fully correctly filled object', async () => {
        // Mock fetch
        global.fetch = jest.fn(() =>
            Promise.resolve({
                json: () => Promise.resolve(mockFetchReturnValue),
            })
        );
        fetch.mockClear();

        await waitFor(() => {
            const {container} = render(<BirdCard bird={birdCompleted}/>);

            let identity = container.getElementsByClassName('specie-identity');
            expect(identity.length).toBe(1);
            expect(identity[0].textContent).toContain('test-en');

            let info = container.getElementsByClassName('specie-info');
            expect(info.length).toBe(1);
            let specieInfoChildren = info[0].children;
            expect(specieInfoChildren.length).toBe(2);
            expect(specieInfoChildren[1].textContent).toContain('test-specie');

            let locInfo = container.getElementsByClassName('location-info');
            expect(locInfo.length).toBe(1);
            let locInfoChildren = locInfo[0].children;
            expect(locInfoChildren.length).toBe(2);
            expect(locInfoChildren[1].textContent).toContain('test-location');

            expect(global.fetch).toHaveBeenCalledTimes(1);
            expect(global.fetch).toHaveBeenCalledWith('https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=test-en&srprop=snippet&format=json&origin=*', {
                'headers': {
                    'Accept': 'application/json',
                    'Accept-Language': 'en-US'
                },
                method: 'GET'
            });

            let img = container.getElementsByClassName('bird-img');
            expect(img.length).toBe(1);
            // Should be JS obj(file as it the resource is mocked) not a link
            expect(img[0].getAttribute('src')).toBe('[object Object]');
        });
    });

    test('Passing partially filled object', async () => {
        global.fetch = jest.fn(() =>
            Promise.reject(new Error('Something went wrong'))
        );
        fetch.mockClear();

        const {container} = render(<BirdCard bird={birdNotCompleted}/>);

        await waitFor(() => {
            let identity = container.getElementsByClassName('specie-identity');
            expect(identity.length).toBe(1);
            expect(identity[0].textContent).toContain('Identity Unknown');

            let img = container.getElementsByClassName('bird-img');
            expect(img.length).toBe(1);
            // Should be JS obj(file as it the resource is mocked) not a link
            expect(img[0].getAttribute('src')).toContain('[object Object]');
        });
    });

})