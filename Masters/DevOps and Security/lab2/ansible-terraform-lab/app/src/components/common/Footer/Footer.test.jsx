import {render} from "@testing-library/react";
import Footer from "./Footer";

describe('Footer tests', () => {
    it('Basic footer render test', () => {
        const {container} = render(<Footer />);

        let header = container.getElementsByTagName('h3');
        expect(header.length).toBe(1);
        expect(header[0].textContent).toContain('© Copyright 2023 🐤 NearBirds');
    });
});
