import {render} from "@testing-library/react";
import Select from "./Select";

let onChange;

describe('Select tests', () => {

    beforeEach(() => {
        onChange = jest.fn();
    })

    it('Basic select render test', () => {
        const {container} = render(<Select country={"Russia"} handleNextCountry={onChange} />);

        let searchLabel = container.getElementsByClassName('search-label');
        expect(searchLabel.length).toBe(1);
        expect(searchLabel[0].textContent).toContain('Select a country');
    });

});
