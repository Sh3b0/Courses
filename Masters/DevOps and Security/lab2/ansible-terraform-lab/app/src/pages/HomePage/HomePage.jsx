import React from 'react'
import Home from "../../components/Home/Home"
import * as Sentry from '@sentry/react';

const HomePage = () => {
  return (
    <Sentry.ErrorBoundary fallback={<p>An error has occurred in HomePage File</p>}>
    <Home/>
    </Sentry.ErrorBoundary>
  )
}

export default HomePage