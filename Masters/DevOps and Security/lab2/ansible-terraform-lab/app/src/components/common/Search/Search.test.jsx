import {render, waitFor} from "@testing-library/react";
import Search from "./Search";
import userEvent from "@testing-library/user-event";

let onChange;

describe('Search tests', () => {

    beforeEach(() => {
        onChange = jest.fn();
    })

    it('Basic search render test', () => {
        const {container} = render(<Search handleSearch={onChange} />);

        let header = container.getElementsByClassName('search-header');
        expect(header.length).toBe(1);
        expect(header[0].textContent).toContain('Search');

        let searchInput = container.getElementsByClassName('search-input');
        expect(searchInput.length).toBe(1);
    });

    it('Typing into the search field test', async () => {
        const {container} = render(<Search handleSearch={onChange} />);

        let searchInput = container.getElementsByClassName('search-input')[0];
        await waitFor(() => {
            userEvent.type(searchInput, 'input');

            expect(onChange).toHaveBeenCalled();
        });
    });
});
