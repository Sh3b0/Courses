package com.monitordemo.demo.DAO.utils;


import java.util.ArrayList;
import java.util.Arrays;

/**
 * RequestValidations is class to validate request data passed
 * @author abliekassama@yahoo.com
 * @version 0.0.1
 */
public class RequestValidations {

    /**
     * Get flight request data and check if they are valid
     *
     * @param website  Website to validate.
     * @param from From location to validate.
     * @param to   To location to validate.
     * @param departDate  Departure date to validate
     * @param returnDate  Return date to validate.
     *
     *@return empty string if all valid
     */
    public boolean IsValidRequest(String website, String from, String to, String departDate, String returnDate){
        boolean validationError = true;
        if(!ValidateWebsite(website)){
            //validationError = validationError + "\nThis airline isn't supported yet";
            validationError = false;
        }
        if(!ValidateLocation(from)){
            //validationError = validationError + "\nRequest needs to have field 'from' or is not in the right format";
            validationError = false;
        }
        if(!ValidateLocation(to)){
            //validationError = validationError + "\nRequest is not in the right format";
            validationError = false;
        }
        if(!ValidateDateDepart(departDate)){
            //validationError = validationError + "\nRequest needs to have field 'departDate' or is not in the right format";
            validationError = false;
        }
        if(!ValidateDateReturn(returnDate)){
            //validationError = validationError + "\nRequest needs to have field 'returnDate' or is not in the right format";
            validationError = false;
        }
        return validationError;
    }

    /**
     * Check if airline website is valid/supported
     *
     * @param website  Takes website text to validate.
     *
     *@return boolean
     */
    public boolean ValidateWebsite(String website) {
        ArrayList<String> airlineWeb = AirlineWebsites();
        if (airlineWeb.contains(website)) {
            return true;
        }
        return false;
    }

    /**
     * Gets list of all supported airlines
     *
     *@return array list
     */
    public static ArrayList<String> AirlineWebsites()    {
        ConfigPropertyValues config = new ConfigPropertyValues();
        return new ArrayList<>(Arrays.asList(config.getPropValues("UnitedWebsite"), config.getPropValues("SouthwestWebsite"), config.getPropValues("AirFranceWebsite"), config.getPropValues("LufthanzaWebsite"), config.getPropValues("AmericanAirlineWebsite")));
    }

    /**
     * Check if flight location is of valid format
     *
     * @param location  Takes location text to validate.
     *
     *@return boolean
     */
    public static boolean ValidateLocation(String location)    {
        if(location.length() > 2 && location.length() < 20) {
            return true;
        }
        return false;
    }


    /**
     * Checks if depart date is of valid format
     *
     * @param date  Takes date text to validate.
     *
     *@return boolean
     */
    public static boolean ValidateDateDepart(String date)    {
        if(date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return true;
        }
        return false;
    }

    /**
     * Checks if return date is of valid format. Null/Empty dates are valid for return date (One way)
     *
     * @param date  Takes date text to validate.
     *
     *@return boolean
     */
    public static boolean ValidateDateReturn(String date)    {
        if(date != null && !date.equals("")){
            return date.matches("\\d{4}-\\d{2}-\\d{2}");
        }
        return true;
    }

}
