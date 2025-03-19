from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
import time

URL = 'http://64.226.66.76'
TITLE = 'Near Birds'


def init_browser():
    options = Options()
    # options.add_argument('--headless')
    chrome_driver = webdriver.Chrome(options=options)
    chrome_driver.implicitly_wait(5)
    chrome_driver.get(URL)
    assert chrome_driver.title == TITLE
    return chrome_driver


def test_search_by_country():
    driver = init_browser()
    driver.find_element(
        by=By.XPATH,
        value="//*[@id='root']/div/div/div[2]/main/div[1]/div[1]/select/option[47]"
    ).click()
    time.sleep(1)
    assert "Egypt" in driver.find_element(
        By.CLASS_NAME,
        value="country-header"
    ).text
    driver.quit()


def test_search_by_bird():
    driver = init_browser()
    search_box = driver.find_element(value='simple-search')
    time.sleep(1)
    search_query = "Red-breasted"
    search_box.send_keys(search_query)
    time.sleep(1)
    assert search_query in driver.find_element(
        By.CLASS_NAME, value="specie-identity").text
    driver.quit()
