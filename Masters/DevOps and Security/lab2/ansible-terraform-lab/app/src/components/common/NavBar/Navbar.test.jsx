import {render} from "@testing-library/react";
import NavBar from "./NavBar";
import ReactTestUtils from 'react-dom/test-utils';


describe('NavBar tests', () => {
    it('Basic navbar render test', () => {
        const {container} = render(<NavBar />);

        let navLinksBlock = container.getElementsByClassName('nav-links');
        expect(navLinksBlock.length).toBe(1);

        // We expect only one link to "/home"
        let navLinks = navLinksBlock[0].children;
        expect(navLinks.length).toBe(1);
        expect(navLinks[0].getElementsByTagName('a').length).toBe(1);
        let homeLink = navLinks[0].getElementsByTagName('a')[0];
        expect(homeLink.textContent).toContain('Home');
        expect(homeLink.getAttribute('href')).toContain('/');
    });

    it('Open and close mobile navbar test', () => {
        const {container} = render(<NavBar />);

        expect(container.getElementsByClassName('menu-outline-open').length).toBe(1);
        let mobileMenuOpen = container.getElementsByClassName('menu-outline-open')[0];
        ReactTestUtils.act(() => {
            // Click to open mobile menu
            mobileMenuOpen.dispatchEvent(new MouseEvent('click', {bubbles: true}));
        });

        // See the button to close mobile menu
        expect(container.getElementsByClassName('menu-outline-close').length).toBe(1);
        let mobileMenuClose = container.getElementsByClassName('menu-outline-close')[0];

        // Close the mobile menu
        ReactTestUtils.act(() => {
            // Click to close mobile menu
            mobileMenuClose.dispatchEvent(new MouseEvent('click', {bubbles: true}));
        });

        // Again see the button to open mobile menu
        expect(container.getElementsByClassName('menu-outline-open').length).toBe(1);
    });
});
