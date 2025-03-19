import React from "react";
import countryList from "../../../Data/CountryList";
import * as Sentry from '@sentry/react';

const Select = ({ country, handleNextCountry }) => {
  const country_list = countryList();

  return (
    <Sentry.ErrorBoundary fallback={<p>An error has occurred in Select File</p>}>

      <div className="space-y-2">
        <label htmlFor="base" className="block mb-3 text-[16px] text-gray-600 search-label">
          Select a country
        </label>
        <select
          value={country}
          onChange={handleNextCountry}
          className="bg-white border border-gray-300 text-gray-900 text-[16px] rounded-lg  w-96 p-2 h-[33px]"
        >
          {country_list.map((item) => (
            <option key={item} value={item}>
              {item}
            </option>
          ))}
        </select>
      </div>
    </Sentry.ErrorBoundary>
  );
};

export default Select;
