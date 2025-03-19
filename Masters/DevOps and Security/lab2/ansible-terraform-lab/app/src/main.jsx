import React from 'react';
import ReactDOM from 'react-dom/client';
import * as Sentry from "@sentry/react";
import { BrowserTracing } from "@sentry/tracing";
import { BrowserRouter as Router,
        useLocation, 
        useNavigationType,
        createRoutesFromChildren,
        matchRoutes } from 'react-router-dom';
import './index.css';
import App from './App';

Sentry.init({
  dsn: "https://00cc45f0b15c447c940b35c414546384@o4504886346711040.ingest.sentry.io/4504886354640896",
  integrations: [ new BrowserTracing({
    routingInstrumentation: Sentry.reactRouterV6Instrumentation(
        React.useEffect,
        useLocation,
        useNavigationType,
        createRoutesFromChildren,
        matchRoutes,
    ),
}),],
  tracesSampleRate: 1.0,
});


ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <Router>
       <App />
    </Router>
  </React.StrictMode>,
);
