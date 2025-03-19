import {render, waitFor} from "@testing-library/react";
import BirdsList from "./BirdsList";

jest.mock('node-fetch');
import fetch from 'node-fetch';

const {Response} = jest.requireActual('node-fetch');

const mockFetchReturnValue = {
    photos: {
        photo: [
            {
                farm: 'test-farm'
            }
        ]
    }
};

const birdsList = [
    {
        id: 1,
        sono: {
            small: "Abc"
        },
        sp: 'test-specie',
        loc: 'test-location',
        en: 'test-en'
    }
];

describe('BirdList tests', () => {

    beforeEach(() => {
        global.fetch = jest.fn(() =>
            Promise.resolve({
                json: () => Promise.resolve(mockFetchReturnValue),
            })
        );
        fetch.mockClear();
    });

    afterEach(() => {
        global.fetch.mockClear()
        delete global.fetch
    });

    test('Passing list with birds of size 1', async () => {
        const {container} = render(<BirdsList birdsList={birdsList} next={1} country={"Russia"} />);

        await waitFor(() => {
            let countryHeader = container.getElementsByClassName('country-header');
            expect(countryHeader.length).toBe(1);
            expect(countryHeader[0].textContent).toContain('Birds in “Russia 🇷🇺”');

            let birdsUl = container.getElementsByClassName('birds-list');
            expect(birdsUl.length).toBe(1);

            // We expect only 1 BirdCard item to be rendered
            let birdsLis = birdsUl[0].children;
            expect(birdsLis.length).toBe(1);
        });
    });

    test('Passing empty list with birds', async () => {
        const {container} = render(<BirdsList birdsList={[]} next={0} country={"China"} />);

        await waitFor(() => {
            let countryHeader = container.getElementsByClassName('country-header');
            expect(countryHeader.length).toBe(1);
            expect(countryHeader[0].textContent).toContain('Birds in “China 🇨🇳”');

            let birdsUl = container.getElementsByClassName('birds-list');
            expect(birdsUl.length).toBe(1);

            // We do not expect BirdCard items
            let birdsLis = birdsUl[0].children;
            expect(birdsLis.length).toBe(0);
        });
    });
});

