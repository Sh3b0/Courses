# Lab 8 - Software Quality and Reliability

[![pipeline status](https://gitlab.com/Sh3B0/sqr-lab8/badges/master/pipeline.svg)](https://gitlab.com/Sh3B0/sqr-lab8/-/commits/master)

Website to be tested: <https://todomvc.com/examples/vue/>

## Exploratory Tests

The following UI tests will be developed for the website

### **Test 1 (adding new items)**

1. Visit the website
2. Click on the input field
3. Enter some text and press enter
4. Check that the item is added to the list
5. Add another item
6. Check that both items are in the list

### **Test 2 (editing existing items)**

1. Visit the website
2. Add two sample items
3. Double click on the second item
4. Enter a different text and press enter key
5. Check that the item is updated

### **Test 3 (tests the item counter)**

1. Visit the website
2. Add a sample item
3. Check that the counter contains the text "1"
4. Add another item
5. Check that the counter contains the text "2"
6. Mark the first item as completed
7. Check that the counter contains the text "1"

## Automation

- [PlayWright](https://playwright.dev/) is used to automate the execution of the above tests.
- GitLab CI file is written to run the tests on new code commits.

## Steps Taken

1. Initialize a Node.js project `npm init -y` and modify `package.json` as needed

2. Write tests explained above in [`tests/app.test.js`](./tests/app.test.js)

3. Install [playwright/test](playwright/test) as a dev dependency: `npm i -D @playwright/test`

4. Setup browser: `npx playwright install chromium`

5. Create `playwright.config.js` with minimal configuration

6. Run tests: `npm run test`

   ![results](https://i.imgur.com/UvgXuJv.png)
