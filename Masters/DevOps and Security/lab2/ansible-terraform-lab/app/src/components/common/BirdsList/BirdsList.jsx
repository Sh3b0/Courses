import React from "react";
import BirdCard from "../BirdCard/BirdCard";
import { country_flags } from "../../../Data/CountryFlags";
import * as Sentry from '@sentry/react';

const BirdsList = ({ birdsList, next, country }) => {
  const flags = country_flags();

  return (
    <Sentry.ErrorBoundary fallback={<p>An error has occurred in BirdsList File</p>}>
        <div className="space-y-9">
          <h3 className="color-2 font-bold text-[24px] country-header">
            Birds in “{country} {flags[country]}”
          </h3>
          <ul className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-12 px-10 md:px-3 lg:px-0 birds-list">
            {birdsList.slice(0, next).map((bird) => (
              <li key={bird.id}>
                <BirdCard bird={bird} />
              </li>
            ))}
          </ul>
        </div>
    </Sentry.ErrorBoundary>
  );
};

export default Sentry.withProfiler(BirdsList);
