package com.monitordemo.demo.DAO.parsers;


import com.monitordemo.demo.DAO.utils.RequestValidations;
import com.monitordemo.demo.DTO.Flight;
import com.monitordemo.demo.DTO.Trip;
import com.monitordemo.demo.DTO.WebParseResponse;
import com.monitordemo.demo.DTO.WebsiteParceRequest;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


import javax.security.auth.message.callback.PrivateKeyCallback;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

/**
 * UnitedAirlinesRest is class to parse UnitedAirlines website with assistance of RestTemplate technology
 * JSONParser is used to parse Data about flights
 * @author BritikovKI@Yandex.ru
 * @version 0.0.1
 */

public class UnitedAirlinesRest {


    RestTemplate restTemplate;
    HttpHeaders headers;
    HttpEntity<String> requestEntity;
    HttpEntity<String> responseEntity;


    public UnitedAirlinesRest(){
        this.restTemplate = new RestTemplate();
        this.headers = new HttpHeaders();
        this.requestEntity =  new HttpEntity<String>("", this.headers);
        this.responseEntity = null;
    }


    /**
     * getFlights function that parses website through requests and get all needed info about flights
     * @param request is request to get flights on certain date(WebsiteParceRequest)
     * @return function returns WebParseResponse which contains all flights on established dates
     */
    public WebParseResponse getFlights(WebsiteParceRequest request) throws IOException, IllegalAccessException, ParseException {


        String from = request.getFrom();
        String to = request.getTo();
        String depart = request.getDepartDate();
        String back = request.getReturnDate();


        //TODO: Fix this shitty code
        if(back.equals("")){
            back = null;
        }
// TODO: Add exceptions in this metho to distinguish connection issues and request issues
        if(from == null || to == null || depart == null){
            throw new IOException();
        }

        headers.add("Accept", "*/*");
        headers.add("Host", "www.united.com");
        headers.add("User-Agent", "insomnia/7.1.1");

        // This is a request to starting page, to begin communication with United Airlines
        initialRequest();
        //Those are requests to establish session and prepare some of the cookies(abck)
        String abck = setupSession();
        //This is request to establish remaining cookie(uapwaul...)
        prepareCookies();
        //This was needed to get to the search page to check that cookies are allright
        getSearchPage(from,to,depart,back,abck);
        if(responseEntity == null)
        {
            throw new IllegalAccessException();
        }
        // request to get information about flights
        LinkedList<Trip> trips = getFlightData(from,to,depart,back);

        if(trips == null){
            throw new NullPointerException();
        }


        return new WebParseResponse(request,trips);
    }


    /**
     * initialRequest function is to establish the connection with UA website
     */
    private void initialRequest(){
        requestEntity = new HttpEntity<String>("", headers);
        responseEntity = restTemplate.exchange("https://www.united.com/en/us", HttpMethod.GET, requestEntity, String.class);
        headers.addAll("Cookie", Objects.requireNonNull(responseEntity.getHeaders().get("Set-Cookie")));

    }

    /**
     * setupSession is to create session and get abck cookie
     * @return abck cookie
     */
    private String setupSession() throws ParseException {
        String abck = "";
        responseEntity = restTemplate.exchange("https://www.united.com/api/token/anonymous", HttpMethod.GET, requestEntity, String.class);
        String bearer = responseEntity.getBody();
        JSONParser parser = new JSONParser();

        JSONObject json = (JSONObject) parser.parse(bearer);
        json =  (JSONObject) parser.parse(json.getAsString("data")) ;
        json =  (JSONObject) parser.parse(json.getAsString("token"));
        bearer = json.getAsString("hash");
        requestEntity = new HttpEntity<String>("", headers);
        String fooResourceUrl="https://www.united.com/api/712beb5c0199b3807dc3a4a287eff";
        abck = (Objects.requireNonNull(headers.get("Cookie")).get(1).split(";")[0]).split("=")[1];
        responseEntity = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, requestEntity, String.class);
        requestEntity = new HttpEntity<>("{\"sensor_data\":\"7a74G7m23Vrp0o5c9040241.54-1,2,-94,-100,Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:72.0) Gecko/20100101 Firefox/72.0,uaend,11059,20100101,en-US,Gecko,1,0,0,0,389484,4675281,1600,860,1600,900,799,177,799,,cpen:0,i1:0,dm:0,cwen:0,non:1,opc:0,fc:1,sc:0,wrc:1,isc:120.5,vib:1,bat:0,x11:0,x12:1,5575,0.614181376307,791482337639.5,loc:-1,2,-94,-101,do_en,dm_en,t_dis-1,2,-94,-105,-1,2,-94,-102,-1,2,-94,-108,-1,2,-94,-110,-1,2,-94,-117,-1,2,-94,-111,-1,2,-94,-109,-1,2,-94,-114,-1,2,-94,-103,-1,2,-94,-112,https://www.united.com/en/us-1,2,-94,-115,1,32,32,0,0,0,0,5,0,1582964675279,-999999,16934,0,0,2822,0,0,9,0,0,"+abck+",29239,-1,-1,25543097-1,2,-94,-106,0,0-1,2,-94,-119,-1-1,2,-94,-122,0,0,0,0,1,0,0-1,2,-94,-123,-1,2,-94,-124,-1,2,-94,-126,-1,2,-94,-127,-1,2,-94,-70,-1-1,2,-94,-80,94-1,2,-94,-116,42077542-1,2,-94,-118,71430-1,2,-94,-121,;8;-1;0\"}",headers);
        responseEntity = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, requestEntity, String.class);
        requestEntity = new HttpEntity<>("{\"sensor_data\":\"7a74G7m23Vrp0o5c9040841.54-1,2,-94,-100,Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:72.0) Gecko/20100101 Firefox/72.0,uaend,11059,20100101,en-US,Gecko,1,0,0,0,389632,6990036,1600,860,1600,900,799,283,799,,cpen:0,i1:0,dm:0,cwen:0,non:1,opc:0,fc:1,sc:0,wrc:1,isc:120.5,vib:1,bat:0,x11:0,x12:1,5575,0.16337392681,791783495017.5,loc:-1,2,-94,-101,do_en,dm_en,t_dis-1,2,-94,-105,-1,2,-94,-102,0,0,0,0,2177,-1,0;0,0,0,0,2715,-1,0;0,0,0,0,990,990,1;0,0,0,0,1022,1022,1;0,0,0,0,2659,0,1;-1,2,-94,-108,-1,2,-94,-110,-1,2,-94,-117,-1,2,-94,-111,-1,2,-94,-109,-1,2,-94,-114,-1,2,-94,-103,-1,2,-94,-112,https://www.united.com/en/us-1,2,-94,-115,1,32,32,0,0,0,0,1632,0,1583566990035,16,16940,0,0,2823,0,0,1634,0,0,"+abck+",33066,81,1836206272,25543097-1,2,-94,-106,9,1-1,2,-94,-119,2000,200,1000,400,400,400,0,200,200,200,200,200,200,600,-1,2,-94,-122,0,0,0,0,1,0,0-1,2,-94,-123,-1,2,-94,-124,-1,2,-94,-126,-1,2,-94,-127,11133333331333333333-1,2,-94,-70,1071633711;-261678837;dis;,3;true;true;true;-180;true;24;24;true;false;1-1,2,-94,-80,5431-1,2,-94,-116,6990035-1,2,-94,-118,83678-1,2,-94,-121,;4;10;0\"}", headers);
        responseEntity = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, requestEntity, String.class);
        Objects.requireNonNull(headers.get("Cookie")).remove(3);
        Objects.requireNonNull(headers.get("Cookie")).remove(1);
        Objects.requireNonNull(headers.get("Cookie")).remove(0);
        headers.addAll("Cookie", Objects.requireNonNull(responseEntity.getHeaders().get("Set-Cookie")));

        headers.add("Accept-Language","en-US,en;q=0.5");
        headers.add("X-Authorization-api","bearer "+bearer);
        return abck;
    }

    /**
     * prepareCookies needed to prepare remaining cookie for request to United airlines(uawp..)
     */
    private void prepareCookies(){
        requestEntity = new HttpEntity<String>("",headers);
        responseEntity = restTemplate.exchange("https://www.united.com/api/home/advisories", HttpMethod.GET,requestEntity, String.class);

        Objects.requireNonNull(headers.get("Cookie")).remove(1);
        headers.addAll("Cookie", Objects.requireNonNull(responseEntity.getHeaders().get("Set-Cookie")));

        ResponseEntity<byte[]> responseEntity1;
        responseEntity = null;
        headers.remove("X-Authorization-api");
    }

    /**
     * getSearchPage function is needed to assure that established cookies are correct
     * @param from department airport
     * @param to destination airport
     * @param depart departure date
     * @param back return date
     * @param abck abck cookie
     */
    private void getSearchPage(String from,String to ,String depart,String back, String abck){
        int i = 0;

        String fooResourceUrl = "";
        do {
            try {
                String cookie = Objects.requireNonNull(headers.get("Cookie")).get(2).split(";")[0]+"; "+Objects.requireNonNull(headers.get("Cookie")).get(1).split(";")[0]+"; "+Objects.requireNonNull(headers.get("Cookie")).get(0).split(";")[0];
                headers.remove("Cookie");
                headers.add("Cookie",cookie);
                if(back!=null){
                    fooResourceUrl = "https://www.united.com/ual/en/US/flight-search/book-a-flight/results/rev?f="+from+"&t="+to+"&d="+depart+"&r="+back+"&sc=7,7&px=1&taxng=1&newHP=True&idx=1";
                }
                else {
                    fooResourceUrl = "https://www.united.com/ual/en/US/flight-search/book-a-flight/results/rev?f="+from+"&t="+to+"&d="+depart+"&sc=7,7&px=1&taxng=1&newHP=True&idx=1";
                }
                requestEntity=new HttpEntity<>("",headers);
                responseEntity = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, requestEntity, String.class);
            } catch (Exception e){
                fooResourceUrl="https://www.united.com/api/712beb5c0199b3807dc3a4a287eff";
                requestEntity = new HttpEntity<>("{\"sensor_data\":\"7a74G7m23Vrp0o5c9040241.54-1,2,-94,-100,Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:72.0) Gecko/20100101 Firefox/72.0,uaend,11059,20100101,en-US,Gecko,1,0,0,0,389484,4675281,1600,860,1600,900,799,177,799,,cpen:0,i1:0,dm:0,cwen:0,non:1,opc:0,fc:1,sc:0,wrc:1,isc:120.5,vib:1,bat:0,x11:0,x12:1,5575,0.09395809346,791482337639.5,loc:-1,2,-94,-101,do_en,dm_en,t_dis-1,2,-94,-105,-1,2,-94,-102,-1,2,-94,-108,-1,2,-94,-110,-1,2,-94,-117,-1,2,-94,-111,-1,2,-94,-109,-1,2,-94,-114,-1,2,-94,-103,-1,2,-94,-112,https://www.united.com/en/us-1,2,-94,-115,1,32,32,0,0,0,0,544,0,1582964675279,27,16934,0,0,2822,0,0,546,0,0,"+abck+",29239,529,-1754468776,25543097-1,2,-94,-106,9,1-1,2,-94,-119,0,200,0,200,200,200,400,0,200,400,400,200,0,600,-1,2,-94,-122,0,0,0,0,1,0,0-1,2,-94,-123,-1,2,-94,-124,-1,2,-94,-126,-1,2,-94,-127,11133333331333333333-1,2,-94,-70,1071633711;-261678837;dis;,3;true;true;true;-180;true;24;24;true;false;1-1,2,-94,-80,5431-1,2,-94,-116,42077542-1,2,-94,-118,75073-1,2,-94,-121,;8;12;0\"}", headers);
                responseEntity = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, requestEntity, String.class);
                Objects.requireNonNull(headers.get("Cookie")).remove(0);
                headers.addAll("Cookie", Objects.requireNonNull(responseEntity.getHeaders().get("Set-Cookie")));
                responseEntity = null;
                i+=1;
            }
        } while (responseEntity == null && i<3);
    }


    /**
     * getFlightData manages requests to get all flights iformation
     * @param from department airport
     * @param to destination airport
     * @param depart departure date
     * @param back date of return
     * @return List of flights
     */
    private LinkedList<Trip> getFlightData(String from, String to, String depart, String back) throws IOException, ParseException {
        headers.add("Accept-Encoding","gzip, deflate, br");
        headers.add("Content-Type","application/json; charset=utf-8");

        String body = formRequest(from,to,depart);
        requestEntity = new HttpEntity<>(body ,headers);
        HttpEntity<byte[]> responseEntityB = restTemplate.exchange("https://www.united.com/ual/en/us/flight-search/book-a-flight/flightshopping/getflightresults/rev",
                HttpMethod.POST, requestEntity, byte[].class);

       String data = "";
       data = decompress(Objects.requireNonNull(responseEntityB.getBody()));
       LinkedList<Trip> trips =  parseFlights(data);
       if(back!=null){
                body = formRequest(to,from,back);
                requestEntity = new HttpEntity<>(body ,headers);
                responseEntityB = restTemplate.exchange("https://www.united.com/ual/en/us/flight-search/book-a-flight/flightshopping/getflightresults/rev",
                        HttpMethod.POST, requestEntity, byte[].class);
                data = decompress(Objects.requireNonNull(responseEntityB.getBody()));
                trips.addAll(parseFlights(data));
       }
       return trips;

    }

    /**
     * decompress function is needed to decompress gziped response to POST request on flight data
     * GZIP is needed because information aou flights might take up to two mbs archived
     * @param str is byte representation of response body
     * @return it returns decompressed JSON in String format
     * @throws IOException it throws exception in case it cannot decompress bytes(they aren't in GZIP)
     */
    private static String decompress(byte[] str) throws IOException {
        if (str == null || str.length== 0) {
            return "";
        }
        System.out.println("Input String length : " + str.length);
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8));
        String outStr = "";
        String line;
        while ((line=bf.readLine())!=null) {
            outStr += line;
        }
        System.out.println("Output String lenght : " + outStr.length());
        return outStr;
    }

    /**
     * parseFlights method is needed to parse decompressed JSON(as a String),
     * and get all necessary information about flights
     * @param data it is JSON with information about flights as a string
     * @return it returns List of all flights listed in response(on the date of departure)
     * @throws ParseException if it couldn't parse JSON for needed information
     */
    private LinkedList<Trip> parseFlights(String data) throws ParseException {;
        LinkedList<Trip> allTrips = new LinkedList<>();
        LinkedList<Flight> flights;
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(data);
        JSONObject equipment;
        JSONObject prices;
        HashMap<String, Float> temp;


        json =  (JSONObject) parser.parse(json.getAsString("data")) ;
        JSONArray trips = (JSONArray) parser.parse(json.getAsString("Trips"));
        JSONArray paths;
        JSONArray list;
        JSONArray tripFlights;
        JSONObject flight;
        for(int i=0;i<trips.size();i++){
            json = (JSONObject) trips.get(i);

            paths =  (JSONArray) parser.parse(json.getAsString("Flights"));
            for(int j=0;j<paths.size();j++){

                json = (JSONObject) paths.get(j);
                list = (JSONArray) parser.parse(json.getAsString("BookingClassAvailList")) ;
                equipment =  (JSONObject) parser.parse(json.getAsString("EquipmentDisclosures")) ;
                prices = (JSONObject) parser.parse(json.getAsString("PricesByColumn")) ;
                temp = new HashMap<>();
                temp.putIfAbsent("ECO-F", Float.parseFloat(prices.getAsString("ECONOMY-FLEXIBLE")));
                temp.putIfAbsent("ECO-B", Float.parseFloat(prices.getAsString("ECO-BASIC")));
                temp.putIfAbsent("BIS", Float.parseFloat(prices.getAsString("MIN-BUSINESS-OR-FIRST")));
                temp.putIfAbsent("ECO", Float.parseFloat(prices.getAsString("ECONOMY")));

                tripFlights = (JSONArray) parser.parse(json.getAsString("FlightSegmentJson"));
                flights = new LinkedList<>();
                for(int k=0;k<tripFlights.size();k++){
                    flight = (JSONObject) tripFlights.get(k);
                    flights.add(
                            new Flight(flight.getAsString("FlightDate"),
                                    json.getAsString("DestinationDateTime"),
                                    String.valueOf(list.size()),
                                    flight.getAsString("FlightNumber"),
                                    equipment.getAsString("EquipmentDescription"),
                                    flight.getAsString("Origin"),
                                    flight.getAsString("Destination")
                            ));

                    }
                flights.getLast().setArrivalTime(json.getAsString("LastDestinationDateTime"));
                allTrips.add(new Trip(json.getAsString("DepartDateTime"),
                        json.getAsString("LastDestinationDateTime"),
                        flights.getFirst().getOrigin(),
                        flights.getLast().getDestination(),
                        flights,
                        temp));
                for(Flight f:flights){
                    allTrips.getLast().getFlightNumbers().add(Integer.parseInt(f.getFlightCode()));
                }
            }

            }

        return allTrips;
    }


    /**
     * formRequest method is needed to format JSON for a post request to a Server(set up needed date, time, etc.)
     * @param from airport of departure
     * @param to airport of destination
     * @param depart date of departure
     * @return Formed JSON request as a string
     */
    String formRequest(String from, String to, String depart){
        return  "{\n" +
                "\t\"Revise\": false,\n" +
                "\t\"UnaccompaniedMinorDisclamer\": false,\n" +
                "\t\"IsManualUpsellFromBasicEconomy\": false,\n" +
                "\t\"StartFlightRecommendation\": false,\n" +
                "\t\"FareWheelOrCalendarCall\": false,\n" +
                "\t\"RelocateRti\": false,\n" +
                "\t\"ConfirmationID\": null,\n" +
                "\t\"searchTypeMain\": \"roundTrip\",\n" +
                "\t\"realSearchTypeMain\": \"roundTrip\",\n" +
                "\t\"Origin\": \""+from+"\",\n" +
                "\t\"Destination\": \""+to+"\",\n" +
                "\t\"DepartDate\": \""+depart+"\",\n" +
                "\t\"DepartDateBasicFormat\": \"2020-04-13\",\n" +
                "\t\"ReturnDate\": \""+depart+"\",\n" +
                "\t\"ReturnDateBasicFormat\": \"2020-04-15\",\n" +
                "\t\"awardTravel\": false,\n" +
                "\t\"MaxTrips\": null,\n" +
                "\t\"numberOfTravelers\": 1,\n" +
                "\t\"numOfAdults\": 1,\n" +
                "\t\"numOfSeniors\": 0,\n" +
                "\t\"numOfChildren04\": 0,\n" +
                "\t\"numOfChildren03\": 0,\n" +
                "\t\"numOfChildren02\": 0,\n" +
                "\t\"numOfChildren01\": 0,\n" +
                "\t\"numOfInfants\": 0,\n" +
                "\t\"numOfLapInfants\": 0,\n" +
                "\t\"travelerCount\": 1,\n" +
                "\t\"revisedTravelerKeys\": null,\n" +
                "\t\"revisedTravelers\": null,\n" +
                "\t\"OriginalReservation\": null,\n" +
                "\t\"RiskFreePolicy\": null,\n" +
                "\t\"EmployeeDiscountId\": null,\n" +
                "\t\"IsUnAccompaniedMinor\": false,\n" +
                "\t\"MilitaryTravelType\": null,\n" +
                "\t\"MilitaryOrGovernmentPersonnelStateCode\": null,\n" +
                "\t\"tripLength\": 2,\n" +
                "\t\"MultiCityTripLength\": null,\n" +
                "\t\"IsParallelFareWheelCallEnabled\": false,\n" +
                "\t\"flexMonth\": null,\n" +
                "\t\"flexMonth2\": null,\n" +
                "\t\"SortType\": null,\n" +
                "\t\"SortTypeV2\": null,\n" +
                "\t\"cboMiles\": null,\n" +
                "\t\"cboMiles2\": null,\n" +
                "\t\"Trips\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"BBXCellIdSelected\": null,\n" +
                "\t\t\t\"BBXSession\": null,\n" +
                "\t\t\t\"BBXSolutionSetId\": null,\n" +
                "\t\t\t\"DestinationAll\": false,\n" +
                "\t\t\t\"returnARC\": null,\n" +
                "\t\t\t\"connections\": null,\n" +
                "\t\t\t\"nonStopOnly\": false,\n" +
                "\t\t\t\"nonStop\": true,\n" +
                "\t\t\t\"oneStop\": true,\n" +
                "\t\t\t\"twoPlusStop\": true,\n" +
                "\t\t\t\"ChangeType\": 0,\n" +
                "\t\t\t\"DepartDate\": \""+depart+"\",\n" +
                "\t\t\t\"ReturnDate\": null,\n" +
                "\t\t\t\"PetIsTraveling\": false,\n" +
                "\t\t\t\"PreferredTime\": \"\",\n" +
                "\t\t\t\"PreferredTimeReturn\": null,\n" +
                "\t\t\t\"Destination\": \""+to+"\",\n" +
                "\t\t\t\"Index\": 1,\n" +
                "\t\t\t\"Origin\": \""+from+"\",\n" +
                "\t\t\t\"Selected\": false,\n" +
                "\t\t\t\"NonStopMarket\": false,\n" +
                "\t\t\t\"FormatedDepartDate\": \"Fri, Apr 13, 2020\",\n" +
                "\t\t\t\"OriginCorrection\": null,\n" +
                "\t\t\t\"DestinationCorrection\": null,\n" +
                "\t\t\t\"OriginAll\": false,\n" +
                "\t\t\t\"Flights\": null,\n" +
                "\t\t\t\"SelectedFlights\": null,\n" +
                "\t\t\t\"OriginTriggeredAirport\": false,\n" +
                "\t\t\t\"DestinationTriggeredAirport\": false,\n" +
                "\t\t\t\"StopCount\": 0,\n" +
                "\t\t\t\"HasNonStopFlights\": false,\n" +
                "\t\t\t\"Ignored\": false,\n" +
                "\t\t\t\"Sequence\": 0,\n" +
                "\t\t\t\"IsDomesticUS\": false,\n" +
                "\t\t\t\"ClearAllFilters\": false\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\t\"nonStopOnly\": 0,\n" +
                "\t\"CalendarOnly\": false,\n" +
                "\t\"Matrix3day\": false,\n" +
                "\t\"InitialShop\": true,\n" +
                "\t\"IsSearchInjection\": false,\n" +
                "\t\"CartId\": \"19E346F3-3464-4F04-9AEB-C651B840EA11\",\n" +
                "\t\"CellIdSelected\": null,\n" +
                "\t\"BBXSession\": null,\n" +
                "\t\"SolutionSetId\": null,\n" +
                "\t\"SimpleSearch\": true,\n" +
                "\t\"RequeryForUpsell\": false,\n" +
                "\t\"RequeryForPOSChange\": false,\n" +
                "\t\"YBMAlternateService\": false,\n" +
                "\t\"ShowClassOfServiceListPreference\": false,\n" +
                "\t\"ShowAvailableOnlyUpgrades\": false,\n" +
                "\t\"SelectableUpgradesOriginal\": null,\n" +
                "\t\"RegionalPremierUpgradeBalance\": 0,\n" +
                "\t\"GlobalPremierUpgradeBalance\": 0,\n" +
                "\t\"AvailablePlusPoints\": 0,\n" +
                "\t\"RegionalPremierUpgrades\": null,\n" +
                "\t\"GlobalPremierUpgrades\": null,\n" +
                "\t\"FormattedAccountBalance\": null,\n" +
                "\t\"GovType\": null,\n" +
                "\t\"TripTypes\": 0,\n" +
                "\t\"RealTripTypes\": 0,\n" +
                "\t\"RealUpgradePath\": false,\n" +
                "\t\"UpgradePath\": false,\n" +
                "\t\"flexible\": false,\n" +
                "\t\"flexibleAward\": false,\n" +
                "\t\"FlexibleDaysAfter\": 0,\n" +
                "\t\"FlexibleDaysBefore\": 0,\n" +
                "\t\"hiddenPreferredConn\": null,\n" +
                "\t\"hiddenUnpreferredConn\": null,\n" +
                "\t\"carrierPref\": 0,\n" +
                "\t\"chkFltOpt\": 0,\n" +
                "\t\"portOx\": 0,\n" +
                "\t\"travelwPet\": 0,\n" +
                "\t\"NumberOfPets\": 0,\n" +
                "\t\"cabinType\": 0,\n" +
                "\t\"cabinSelection\": \"ECONOMY\",\n" +
                "\t\"awardCabinType\": 0,\n" +
                "\t\"FareTypes\": 0,\n" +
                "\t\"FareWheelOnly\": false,\n" +
                "\t\"EditSearch\": false,\n" +
                "\t\"buyUpgrade\": 0,\n" +
                "\t\"offerCode\": null,\n" +
                "\t\"IsPromo\": false,\n" +
                "\t\"TVAOfferCodeLastName\": null,\n" +
                "\t\"ClassofService\": null,\n" +
                "\t\"UpgradeType\": null,\n" +
                "\t\"AdditionalUpgradeIds\": null,\n" +
                "\t\"SelectedUpgradePrices\": null,\n" +
                "\t\"BillingAddressCountryCode\": null,\n" +
                "\t\"BillingAddressCountryDescription\": null,\n" +
                "\t\"IsPassPlusFlex\": false,\n" +
                "\t\"IsPassPlusSecure\": false,\n" +
                "\t\"IsOffer\": false,\n" +
                "\t\"IsMeetingWorks\": false,\n" +
                "\t\"IsValidPromotion\": false,\n" +
                "\t\"IsCorporate\": 0,\n" +
                "\t\"CalendarDateChange\": null,\n" +
                "\t\"CoolAwardSpecials\": false,\n" +
                "\t\"LastResultId\": null,\n" +
                "\t\"IncludeLmx\": false,\n" +
                "\t\"NGRP\": false,\n" +
                "\t\"calendarStops\": 4,\n" +
                "\t\"IsAwardNonStopDisabled\": false,\n" +
                "\t\"IsWeeklyAwardCalendarEnabled\": true,\n" +
                "\t\"IsMonthlyAwardCalendarEnabled\": true,\n" +
                "\t\"AwardCalendarType\": 0,\n" +
                "\t\"IsAwardCalendarEnabled\": true,\n" +
                "\t\"IsAwardCalendarNonstop\": false,\n" +
                "\t\"corporateBooking\": false,\n" +
                "\t\"IsCorporateLeisure\": false,\n" +
                "\t\"CorporateDiscountCode\": \"\",\n" +
                "\t\"IsAutoUpsellFromBasicEconomy\": false,\n" +
                "\t\"CurrencyDescription\": \"International POS Cuurency\",\n" +
                "\t\"CurrentTripIndex\": 0,\n" +
                "\t\"LowestNonStopEconomyFare\": 0,\n" +
                "\t\"FromFlexibleCalendar\": false,\n" +
                "\t\"TripIndex\": 0,\n" +
                "\t\"Cached\": {\n" +
                "\t\t\"UaSessionId\": \"1e241158-eca7-4914-bae8-a42b74f67aa6\",\n" +
                "\t\t\"CarrierPref\": 0,\n" +
                "\t\t\"PreferredConn\": null,\n" +
                "\t\t\"UnpreferredConn\": null,\n" +
                "\t\t\"HiddenPreferredConn\": null,\n" +
                "\t\t\"HiddenUnpreferredConn\": null,\n" +
                "\t\t\"Trips\": [\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"NonStop\": true,\n" +
                "\t\t\t\t\"OneStop\": true,\n" +
                "\t\"numOfChildren03\": 0,\n" +
                "\t\"numOfChildren02\": 0,\n" +
                "\t\"numOfChildren01\": 0,\n" +
                "\t\"numOfInfants\": 0,\n" +
                "\t\t\t\t\"TwoPlusStop\": true,\n" +
                "\t\t\t\t\"PreferredTime\": \"\",\n" +
                "\t\t\t\t\"PreferredTimeReturn\": null,\n" +
                "\t\t\t\t\"ClearAllFilters\": false\n" +
                "\t\t\t}\n"+
                "\t\t]\n" +
                "\t},\n" +
                "\t\"isReshopPath\": false\n" +
                "}";
    }


}

