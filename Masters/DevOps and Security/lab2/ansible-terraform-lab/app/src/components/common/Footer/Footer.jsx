import React from "react";
import * as Sentry from '@sentry/react';

const Footer = () => {
  return (
      <Sentry.ErrorBoundary fallback={<p>An error has occurred in Footer File</p>}>
        <div className="py-12">
          <h3 className="text-center color-3">© Copyright 2023 🐤 NearBirds</h3>
        </div>
      </Sentry.ErrorBoundary>
  );
};

export default Sentry.withProfiler(Footer);
