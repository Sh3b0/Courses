package com.monitordemo.demo.DAO.parsers;

import com.monitordemo.demo.DAO.utils.ConfigPropertyValues;
import com.monitordemo.demo.DAO.utils.UrlProcessor;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.StringReader;

public class UnitedAirlinesSelenium {

    /**
     * Processes valid request for united airline flight data
     *
     * @param website  Website to validate.
     * @param from From location to validate.
     * @param to   To location to validate.
     * @param departDate  Departure date to validate
     * @param departDate  Return date to validate.
     *
     *@return json string of complete flight data
     */
    public static String ParseUnitedAirline(String website,String from, String to,String departDate,String returnDate){
        //TODO: add constructor and define geckodriver only once
        //Setting WebDriver PATH for geckodriver.
        if(SystemUtils.IS_OS_WINDOWS){
            System.setProperty("webdriver.gecko.driver", GetProjectDir()+"\\browsers\\geckodriver.exe");
        }
        else if(SystemUtils.IS_OS_LINUX){
            System.setProperty("webdriver.gecko.driver", GetProjectDir()+"/browsers/geckodriver");
        }
        else{
            //default windows
            System.setProperty("webdriver.gecko.driver", GetProjectDir()+"\\browsers\\geckodriver.exe");
        }

        //Set Firefox Headless mode as TRUE
        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(true);

        //Instantiate Web Driver
        WebDriver driver = new FirefoxDriver(options);

        //Get home url to pass, required before going to flight results page
        ConfigPropertyValues config = new ConfigPropertyValues();
        driver.get(UrlProcessor.getFlightHomeUrl(config.getPropValues("UnitedWebsite")));

        //Get url format for united
        String flight_url = UrlProcessor.getFlightUrl(website,from,to,departDate,returnDate);

        driver.get(flight_url);

        //Set web driver wait to 20 seconds. Waits till it receives the element with id = fare-select-button
        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.elementToBeClickable(By.className(GetHtmlElement("WaitCondition"))));

        String pageSource = driver.findElement(By.id(GetHtmlElement("DataDiv"))).getText();

        String FlightsJsonTop = "{'website': '"+website+"', 'from': '"+from+"','to': '"+to+"', 'departDate': '"+departDate+"', 'returnDate': '"+returnDate+"', 'flights': [";
        String flightsJsonFormat = ReturnFlightJson(pageSource);
        String FlightsJsonBottom = "]}";
        String FlightsJsonComplete = (FlightsJsonTop + flightsJsonFormat + FlightsJsonBottom).replaceAll("'","\"");

        return FlightsJsonComplete;
    }


    /**
     * Appends flights in json string format
     *
     * @param flights takes the flight text to be processed
     *
     *@return flights json data
     */
    public static String ReturnFlightJson(String flights){
        //Split flights into sections using the separator 'Departing'
        String[] flightsArray = flights.split("Departing");
        String jsonData = "";

        //Set Flight data
        String departTime = "";
        String arrivalTime = "";
        String ticketNumber = "";
        String flightCode = "";
        String plane = "";

        for (String item : flightsArray){

            //If string contains 'Arriving' then it is a flight data
            if(item.contains("Arriving")){
                //Get Flight data
                departTime = ReturnSpecificFlightData(item, "departTime");
                arrivalTime = ReturnSpecificFlightData(item, "arrivalTime");
                ticketNumber = ReturnSpecificFlightData(item, "ticketNumber");
                flightCode = ReturnSpecificFlightData(item, "flightCode");
                plane = ReturnSpecificFlightData(item, "plane");
                //append string to json text
                jsonData = jsonData + "{'departTime': '"+departTime+"', 'arrivalTime': '"+arrivalTime+"', 'ticketNumber': '"+ticketNumber+"', 'flightCode': '"+flightCode+"', 'plane': '"+plane+"'},";
            }
        }

        //remove comma if at the end
        return jsonData.replaceAll(",$", "");
    }



    /**
     * Takes specific flight data and retrieves the required flight info
     *
     * @param flightData takes the flight text to be processed
     * @param return_data takes the data to be returned
     *
     *@return specific flight text data
     */
    public static String ReturnSpecificFlightData(String flightData, String return_data){

        BufferedReader flightsBufReader = new BufferedReader(new StringReader(flightData));
        String line = null;

        boolean  departTimeRead = false;
        boolean  arrivalTimeRead = false;
        boolean  ticketNumberRead = false;
        boolean  flightCodeRead = false;
        boolean  planeRead = false;

        //Flight data
        String departTime = "";
        String arrivalTime = "";
        String ticketNumber = "";
        String flightCode = "";
        String plane = "";

        try{
            while( (line = flightsBufReader.readLine()) != null )
            {
                //First occurrence of : is "from"
                if(line.contains(":") && !departTimeRead){
                    departTime = line;
                    departTimeRead = true;
                }
                //Second occurrence of : with from already read is "to"
                if(line.contains(":") && departTimeRead){
                    arrivalTime = line;
                    arrivalTimeRead = true;
                }

                //TODO - check for ticketNumber, flightCode, plane
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        //Return specific data
        if(return_data.equals("departTime")){
            return departTime;
        }
        else if(return_data.equals("arrivalTime")){
            return arrivalTime;
        }
        else if(return_data.equals("ticketNumber")){
            return ticketNumber;
        }
        else if(return_data.equals("flightCode")){
            return flightCode;
        }
        else if(return_data.equals("plane")){
            return plane;
        }
        return null;
    }

    /**
     * Get the root directory of the project
     *
     *@return root directory path as text
     */
    public static String GetProjectDir(){

        return System.getProperty("user.dir");
    }

    /**
     * Get the css class or id to work with
     *
     *@return a css class or id as text
     */
    public static String GetHtmlElement(String element){
        if(element == "WaitCondition"){
            return "fare-select-button";
        }
        else if(element == "DataDiv"){
            return "fl-results";
        }
        return null;
    }

}
