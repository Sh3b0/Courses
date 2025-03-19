import React, { useState } from "react";
import { AiOutlineGithub } from "react-icons/ai";
import { IoMenuOutline } from "react-icons/io5";
import { IoCloseOutline } from "react-icons/io5";
import logo from "../../../assets/logo.png";
import * as Sentry from '@sentry/react';

const NavBar = () => {
    const [isNavOpen, setIsNavOpen] = useState(false);

    return (
      <Sentry.ErrorBoundary fallback={<p>An error has occurred in NavBar File</p>}>
        <div>
            <div className="flex items-center justify-between px-7 md:px-32 py-5 border-b-2 border-gray-100">
                <div className="flex md:items-center  md:gap-32">
                    <a href="/">
                        <img className="w-[120px] h-[20px]" src={logo} alt="logo"/>
                    </a>
                    <ul className="hidden md:flex items-center gap-7 color-2 nav-links">
                        <li>
                            <a href="/" className="nav-links-home">Home</a>
                        </li>
                    </ul>
                </div>
                {!isNavOpen && (
                    <IoMenuOutline
                        onClick={() => setIsNavOpen(true)}
                        className="text-5xl flex md:hidden hover:cursor-pointer menu-outline-open"
                    />
                )}
                {isNavOpen && (
                    <IoCloseOutline
                        onClick={() => setIsNavOpen(false)}
                        className="text-5xl flex md:hidden hover:cursor-pointer menu-outline-close"
                    />
                )}
                <ul className=" hidden md:flex  items-center gap-8">
                    <li>
                        <a href="https://github.com/NearByrds/NearBirds" target="_blank">
                            <AiOutlineGithub className="color-1 w-8 h-8"/>
                        </a>
                    </li>
                </ul>
            </div>

            {isNavOpen && (
                <div className="absolute z-1000 back-1 left-0 right-0 md:hidden">
                    <div className="flex flex-col pt-16 pb-8  items-center space-y-4 text-[16px]">
                        <ul className="flex flex-col gap-5 color-2">
                            <li>
                                <a href="/">Home</a>
                            </li>
                        </ul>
                        <ul className="flex  items-center gap-8">
                            <li>
                                <a href="https://github.com/NearByrds/NearBirds" target="_blank">
                                    <AiOutlineGithub className="color-1 w-8 h-8"/>
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>
            )}
        </div>
    </Sentry.ErrorBoundary>
    );
};

export default Sentry.withProfiler(NavBar);
