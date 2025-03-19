import {Route, Routes} from "react-router-dom";
import HomePage from "./pages/HomePage/HomePage";
import * as Sentry from "@sentry/react";

const SentryRoutes = Sentry.withSentryReactRouterV6Routing(Routes)

const App = () => {
    return (
        <div>
            <SentryRoutes>
                <Route exact path="/" element={<HomePage/>}/>
            </SentryRoutes>
        </div>
    );
};

export default Sentry.withProfiler(App);
