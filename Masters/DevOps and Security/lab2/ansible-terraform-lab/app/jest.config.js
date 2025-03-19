module.exports = {
    moduleNameMapper: {
        "\\.(css|less)$": "<rootDir>/__mocks__/fileMocks.js",
        "\\.(gif|png|jpg|ttf|woff|woff2)$": "<rootDir>/__mocks__/fileMocks.js",
    },
    testEnvironment: "node",
    testRegex: "/*.test.jsx$",
    collectCoverage: true,
    coverageReporters: ["lcov"],
    coverageDirectory: "test-coverage",
    coverageThreshold: {
        global: {
            branches: 75,
            functions: 75,
            lines: 75,
            statements: 75
        }
    }
};