package com.monitordemo.demo.DAO.parsers;

import com.monitordemo.demo.DTO.Flight;
import com.monitordemo.demo.DTO.Trip;
import com.monitordemo.demo.DTO.WebParseResponse;
import com.monitordemo.demo.DTO.WebsiteParceRequest;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * It is class to parse TripActions website
 */
public class TripActionsParser {

    // Won't be needed when we will have API
    JSONParser parser = new JSONParser();

/*
    public WebParseResponse getFlights(WebsiteParceRequest request){
        String from = null;
        String to = null;
        String depart = null;
        String back = null;
        try {
            from = request.getFrom();
            to = request.getTo();
            depart = request.getDepartDate();
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            back = request.getReturnDate();
        } catch (Exception e){
            e.printStackTrace();
        }
        //TODO: HERE will be implemented part connected to TripActions, when we will get API
        return new WebParseResponse(request,new LinkedList<Flight>());
    }
    */
// TODO: Temporary parser

    /**
     * getFlightsMock is getting data from mocks, established in the resources foulder
     * @param request it is initial request to understand what website we will need to parse
     * @param i it is number of mock we are picking to parse
     * @return it returns list of parsed websites inside of the WebParseResponse
     * @throws FileNotFoundException it is thrown if mock file wasn't found
     * @throws ParseException it is thrown if it is impossible to parse mock
     */
    public WebParseResponse getFlightsMock(WebsiteParceRequest request, int i) throws FileNotFoundException, ParseException {
        Object obj = parser.parse(new FileReader("./src/main/resources/TripActionsMockData/resp"+String.valueOf(i)+".json"));
        JSONObject jsonObject =  (JSONObject) obj;
        LinkedList<Trip> trips = responseParser(request,jsonObject);
        return new WebParseResponse(request,trips);
    }


    /**
     * responseParser is a function which parses JSON of the response
     * @param request info about flight
     * @param data response itself
     * @return parsed List of flights
     * @throws ParseException  it is thrown if it is impossible to parse JSON
     */
    public LinkedList<Trip> responseParser(WebsiteParceRequest request, JSONObject data) throws ParseException {
        LinkedList<Trip> all_trips = new LinkedList<>();

        JSONParser parser = new JSONParser();
        JSONObject prices;
        JSONObject price;
        JSONObject brand;
        JSONObject trip;
        JSONArray fares;
        HashMap<String,Float> temp;
        JSONObject json = data;
        LinkedList<Flight> flights;
        JSONObject flight;
        JSONArray flight_info;
        JSONArray trips = (JSONArray) parser.parse(json.getAsString("options"));
        for(int i=0;i<trips.size();i++){
            trip = (JSONObject) trips.get(i);

            flight =  (JSONObject) parser.parse(trip.getAsString("flight"));
            flight_info = (JSONArray) parser.parse(flight.getAsString("flightSegments"));
            flights = new LinkedList<>();
            for(int j=0;j<flight_info.size();j++){
                json = (JSONObject) flight_info.get(j);
                if(json.getAsString("airlineName").toLowerCase().equals(request.getWebsite().toLowerCase())){
                    flights.add(new Flight(timeFormat(json.getAsString("departureDateAndTime")),
                        timeFormat(json.getAsString("arrivalDateAndTime")),
                        String.valueOf(flight_info.size()),
                        json.getAsString("flightNumber"),
                        json.getAsString("airplaneName"),
                        json.getAsString("departureAirportCode"),
                        json.getAsString("arrivalAirportCode")
                    ));
                }
            }
            if (flights.size()>0){
                all_trips.add(new Trip(flights.getFirst().getDepartTime(),flights.getLast().getArrivalTime(),
                    flights.getFirst().getOrigin(), flights.getLast().getDestination(),flights,new HashMap<>()));
                prices = (JSONObject) parser.parse(trip.getAsString("fares")) ;
                fares = (JSONArray) parser.parse(prices.getAsString("fares")) ;
                temp = new HashMap<>();
                for(int k=0;k<fares.size();k++){
                    price = (JSONObject) parser.parse(fares.get(k).toString());
                    brand = (JSONObject) parser.parse(price.getAsString("brand"));
                    if (json.getAsString("airlineName").toLowerCase().equals("united")) {
                        switch (brand.getAsString("displayName")){
                            case "Economy":
                                temp.putIfAbsent("ECO-B", Float.parseFloat(price.getAsString("totalPriceAndFee")));
                                break;
                            case "United First":
                                temp.putIfAbsent("ECO", Float.parseFloat(price.getAsString("totalPriceAndFee")));
                                break;
                            case "Economy Flexible":
                                temp.putIfAbsent("ECO-F", Float.parseFloat(price.getAsString("totalPriceAndFee")));
                                break;
                            case "First Or Business":
                                temp.putIfAbsent("BIS", Float.parseFloat(price.getAsString("totalPriceAndFee")));
                                break;
                            default:
                                temp.putIfAbsent("BIS", Float.parseFloat(price.getAsString("totalPriceAndFee")));
                                break;
                        }
                    }
                }
                all_trips.getLast().setClassPrices(temp);
//                temp = new HashMap<>();
//                temp.putIfAbsent("ECO-F", Float.parseFloat(prices.getAsString("ECONOMY-FLEXIBLE")));
//                temp.putIfAbsent("ECO-B", Float.parseFloat(prices.getAsString("ECO-BASIC")));
//                temp.putIfAbsent("BIS", Float.parseFloat(prices.getAsString("MIN-BUSINESS-OR-FIRST")));
//                temp.putIfAbsent("ECO", Float.parseFloat(prices.getAsString("ECONOMY")));

                for(Flight f:flights){
                    all_trips.getLast().getFlightNumbers().add(Integer.parseInt(f.getFlightCode()));
                }
            }
        }
        return all_trips;
    }

    private String timeFormat(String time){
        String year = time.substring(0,4);
        String month = time.substring(5,7);
        String day = time.substring(8,10);
        String hour = time.substring(11,13);
        String minute = time.substring(14,16);
        return month+'/'+day+'/'+year+' '+hour+':'+minute;
    }
}
