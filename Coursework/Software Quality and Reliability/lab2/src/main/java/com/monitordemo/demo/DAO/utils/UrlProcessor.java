package com.monitordemo.demo.DAO.utils;


/**
 * UrlProcessor is class to process and generate flight url
 * @author abliekassama@yahoo.com
 * @version 0.0.1
 */
public class UrlProcessor {

    /**
     * Get flight request data and check if they are valid
     *
     * @param website  Website to validate.
     * @param from From location to validate.
     * @param to   To location to validate.
     * @param departDate  Departure date to validate
     * @param returnDate  Return date to validate.
     *
     *@return the url for the specific airline
     */
    public static String getFlightUrl(String website, String from, String to, String departDate, String returnDate){
        ConfigPropertyValues config = new ConfigPropertyValues();
        if(website.equals(config.getPropValues("UnitedWebsite"))){
            return formatUnitedAirlineUrl(from, to, departDate, returnDate);
        }
        else if(website.equals(config.getPropValues("SouthwestWebsite"))){
            return null;
        }
        else if(website.equals(config.getPropValues("LufthanzaWebsite"))){
            return null;
        }
        else if(website.equals(config.getPropValues("AirFranceWebsite"))){
            return null;
        }
        else if(website.equals(config.getPropValues("AmericanAirlineWebsite"))){
            return null;
        }
        //returns null default
        return null;
    }


    /**
     * Check if airline website is valid/supported
     *
     * @param website  Takes website text to validate.
     *
     *@return the home url for the specific airline
     */
    public static String getFlightHomeUrl(String website){
        ConfigPropertyValues config = new ConfigPropertyValues();
        if(website.equals(config.getPropValues("UnitedWebsite"))){
            return "https://www.united.com/en/us";
        }
        else if(website.equals(config.getPropValues("SouthwestWebsite"))){
            return "https://www.southwest.com/";
        }
        else if(website.equals(config.getPropValues("LufthanzaWebsite"))){
            return "https://www.lufthansa.com/ru/en/homepage";
        }
        else if(website.equals(config.getPropValues("AirFranceWebsite"))){
            return "https://www.airfrance.com/indexCom_en.html";
        }
        else if(website.equals(config.getPropValues("AmericanAirlineWebsite"))){
            return "https://www.aa.com/homePage.do";
        }
        //returns united airline as default
        return "https://www.united.com/en/us";
    }


    /**
     * Formats united airline flight request
     *
     * @param from From location to validate.j
     * @param to   To location to validate.
     * @param departDate  Departure date to validate
     * @param returnDate  Return date to validate.
     *
     *@return the flight url for the specific airline flight
     */
    public static String formatUnitedAirlineUrl(String from, String to, String departDate, String returnDate){
        String url = "https://www.united.com/ual/en/US/flight-search/book-a-flight/results/rev?f="+GetLocationAcronym(from)+"&t="+GetLocationAcronym(to)+"&d="+GetLocationAcronym(departDate)+"&r="+GetLocationAcronym(returnDate)+"&sc=7,7&px=1&taxng=1&newHP=True&idx=1";
        if(returnDate == null || returnDate == ""){
            url = "https://www.united.com/ual/en/US/flight-search/book-a-flight/results/rev?f="+GetLocationAcronym(from)+"&t="+GetLocationAcronym(to)+"&d="+GetLocationAcronym(departDate)+"&tt=1&sc=7&px=1&taxng=1&newHP=True&idx=1";
        }
        return url;
    }


    /**
     * Gets destination acronym for locations like: Miami MFL --> MFL, Houston IAH --> IAH
     *
     * @param location  Takes location text to validate.
     *
     *@return location short name
     */
    public static String GetLocationAcronym(String location){
        if(location.contains(" ") && location.length() > 3){
            return location.substring(location.length() - 3).toUpperCase();
        }
        return location.toUpperCase();
    }

}
