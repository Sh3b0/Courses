import React from "react";
import loader from "../../../assets/bird-loader.gif";
import * as Sentry from '@sentry/react';

const Loader = () => {
  return (
    <Sentry.ErrorBoundary fallback={<p>An error has occurred in Loader File</p>}>
        <div className="flex justify-center items-center h-screen blured">
          <img src={loader} alt="bird-loader" className="img-loader" />
        </div>
      </Sentry.ErrorBoundary>
  );
};

export default Loader;
