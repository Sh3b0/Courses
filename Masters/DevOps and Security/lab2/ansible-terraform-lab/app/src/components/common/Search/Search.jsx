import React from "react";
import {HiOutlineSearch} from "react-icons/hi";
import * as Sentry from '@sentry/react';

const Search = ({handleSearch}) => {
    return (
        <Sentry.ErrorBoundary fallback={<p>An error has occurred in Search File</p>}>
            <div className="flex flex-col gap-4 ">
                <span className="text-gray-700 text-[16px] search-header">Search</span>
                <div className="flex gap-2 items-center w-96 border border-gray-300 pl-5 p-[6px] rounded-lg">
                    <HiOutlineSearch className="color-1"/>
                    <input
                        type="text"
                        id="simple-search"
                        className="text-gray-900 text-[16px]  focus:outline-none search-input"
                        placeholder="Search a bird"
                        required
                        onChange={handleSearch}
                    />
                </div>
            </div>
        </Sentry.ErrorBoundary>
    );
};

export default Sentry.withProfiler(Search);
