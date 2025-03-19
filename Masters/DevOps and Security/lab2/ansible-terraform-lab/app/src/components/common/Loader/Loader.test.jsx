import {render} from "@testing-library/react";
import Loader from "./Loader";

describe('Loader tests', () => {
    it('Basic loader render test', () => {
        const {container} = render(<Loader />);

        let imgLoader = container.getElementsByClassName('img-loader');
        expect(imgLoader.length).toBe(1);
    });
});
