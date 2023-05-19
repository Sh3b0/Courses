package com.monitordemo.demo.controllers;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.monitordemo.demo.DAO.DataValidator;
import com.monitordemo.demo.DAO.parsers.TripActionsParser;
import com.monitordemo.demo.DAO.parsers.UnitedAirlinesRest;
import com.monitordemo.demo.DAO.parsers.UnitedAirlinesSelenium;
import com.monitordemo.demo.DAO.utils.ConfigPropertyValues;
import com.monitordemo.demo.DTO.ValidationResponse;
import com.monitordemo.demo.DTO.WebParseResponse;
import com.monitordemo.demo.DTO.WebsiteParceRequest;
import net.minidev.json.parser.ParseException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;


@RestController
@RequestMapping("/parse")
/**@author BritikovKI
  *@version 0,1
  *parserController class that is needed to experiment with parsing webpages*/
public class parserController {

//    _____  ______  _____ _______   _______ ______ __  __ _____  _            _______ ______
//   |  __ \|  ____|/ ____|__   __| |__   __|  ____|  \/  |  __ \| |        /\|__   __|  ____|
//   | |__) | |__  | (___    | |       | |  | |__  | \  / | |__) | |       /  \  | |  | |__
//   |  _  /|  __|  \___ \   | |       | |  |  __| | |\/| |  ___/| |      / /\ \ | |  |  __|
//   | | \ \| |____ ____) |  | |       | |  | |____| |  | | |    | |____ / ____ \| |  | |____
//   |_|  \_\______|_____/   |_|       |_|  |______|_|  |_|_|    |______/_/    \_\_|  |______|
//
//

    /**parsePage method is needed to coordinate page parsing(to define which website we will parse)
      *@param request - is an entity which consists of website to parse, and parameters of searched flights*/
    @PostMapping(path = "/rest",consumes = "application/json",produces = "application/json")
    private ResponseEntity parsePage(@RequestBody WebsiteParceRequest request) throws ParseException {

        if(request.getWebsite()==null){
            return new ResponseEntity<>("Specify_website_to_parse", HttpStatus.BAD_REQUEST);
        }

        ConfigPropertyValues config = new ConfigPropertyValues();
        if(request.getWebsite().equals(config.getPropValues("UnitedWebsite"))){
            UnitedAirlinesRest parser = new UnitedAirlinesRest();
            try{

                WebParseResponse data_UA = parser.getFlights(request);
                if ( data_UA != null) {
                    return new ResponseEntity<>(data_UA, HttpStatus.OK);
                }

            }catch (NullPointerException e){
                return new ResponseEntity<>("There_are_no_flights_for_this_parameters(check_your_inputs);", HttpStatus.NOT_FOUND);
            } catch (IOException e){
                return new ResponseEntity<>("Request_need_to_have_fields:from;to;departDate.", HttpStatus.BAD_REQUEST);
            } catch (IllegalAccessException e){
                return new ResponseEntity<>("There_are_issues_with_achieving_data_from_server(possibly_changes_in_code).", HttpStatus.CONFLICT);
            }
        }
        else if(request.getWebsite().equals(config.getPropValues("SouthwestWebsite"))){
            return null;
        }
        else if(request.getWebsite().equals(config.getPropValues("LufthanzaWebsite"))){
            return null;
        }
        else if(request.getWebsite().equals(config.getPropValues("AirFranceWebsite"))){
            return null;
        }
        else if(request.getWebsite().equals(config.getPropValues("AmericanAirlineWebsite"))){
            return null;
        }
        else {
            //default
            return new ResponseEntity<>("This_aircompany_isn't_supported_yet.", HttpStatus.NOT_IMPLEMENTED);
        }

        return new ResponseEntity<>("Request_is_incorrect.", HttpStatus.I_AM_A_TEAPOT);
    }



    @PostMapping(path = "/verify",consumes = "application/json",produces = "application/json")
    private ResponseEntity verifyFlights(@RequestBody WebsiteParceRequest request) throws ParseException {

        if(request.getWebsite()==null){
            return new ResponseEntity<>("Specify_website_to_parse", HttpStatus.BAD_REQUEST);
        }

        ConfigPropertyValues config = new ConfigPropertyValues();
        TripActionsParser parser_trip = new TripActionsParser();
        DataValidator validator = new DataValidator();
        if(request.getWebsite().equals(config.getPropValues("UnitedWebsite"))){
            /* You should put your calls for classes/methods in dao here
             * it should look like:
             * UnitedAirlinesSelenium parser = new UnitedAirlinesSelenium();
             * return new ResponseEntity<JSONObject>(parser.getFlights(request), HttpStatus.OK);
             * */

            UnitedAirlinesRest parser = new UnitedAirlinesRest();
            try{

                WebParseResponse data_tA = parser_trip.getFlightsMock(request,3);
                WebParseResponse data_UA = parser.getFlights(request);
                if (data_tA != null && data_UA != null) {
                    ValidationResponse response = validator.validate(data_UA.getTrips(),data_tA.getTrips());
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }

            }catch (FileNotFoundException e ){
                return new ResponseEntity<>("Error_with_chosen_mock_for_trip_actions(Not_Found);", HttpStatus.NOT_FOUND);
            }
            catch (NullPointerException e){
                return new ResponseEntity<>("There_are_no_flights_for_this_parameters(check_your_inputs);", HttpStatus.NOT_FOUND);
            } catch (IOException e){
                return new ResponseEntity<>("Request_need_to_have_fields:from;to;departDate.", HttpStatus.BAD_REQUEST);
            } catch (IllegalAccessException e){
                return new ResponseEntity<>("There_are_issues_with_achieving_data_from_server(possibly_changes_in_code).", HttpStatus.CONFLICT);
            }
        }
        else if(request.getWebsite().equals(config.getPropValues("SouthwestWebsite"))){
            return null;
        }
        else if(request.getWebsite().equals(config.getPropValues("LufthanzaWebsite"))){
            return null;
        }
        else if(request.getWebsite().equals(config.getPropValues("AirFranceWebsite"))){
            return null;
        }
        else if(request.getWebsite().equals(config.getPropValues("AmericanAirlineWebsite"))){
            return null;
        }
        else {
            //default
            return new ResponseEntity<>("This_aircompany_isn't_supported_yet.", HttpStatus.NOT_IMPLEMENTED);
        }

        return new ResponseEntity<>("Request_is_incorrect.", HttpStatus.I_AM_A_TEAPOT);
    }



//     _____ ______ _      ______ _   _ _____ _    _ __  __
//    / ____|  ____| |    |  ____| \ | |_   _| |  | |  \/  |
//   | (___ | |__  | |    | |__  |  \| | | | | |  | | \  / |
//    \___ \|  __| | |    |  __| | . ` | | | | |  | | |\/| |
//    ____) | |____| |____| |____| |\  |_| |_| |__| | |  | |
//   |_____/|______|______|______|_| \_|_____|\____/|_|  |_|
//
//

    /**
     * Check if airline website is valid/supported
     *
     * @param parser gets json request posted
     *
     *@return flights json data
     */
    @RequestMapping(path = "/selenium",method = RequestMethod.POST, consumes = "application/json")
    public String getData(@RequestBody WebsiteParceRequest parser) {
        String website =  parser.getWebsite() ;
        String from = parser.getFrom();
        String to = parser.getTo();
        String departDate = parser.getDepartDate();
        String returnDate = parser.getReturnDate();

        UnitedAirlinesSelenium seleniumParser = new UnitedAirlinesSelenium();

        try{
            //Get flights json string
            String flightsJsonObject = seleniumParser.ParseUnitedAirline(website, from, to, departDate, returnDate);

            //Pretty print jsonjust a coo
            Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
            String prettyJsonString = prettyGson.toJson(flightsJsonObject);

            //return string json
            return flightsJsonObject;
        }catch (Exception ex){
            return "There are no flights for this parameters (check_your_inputs)";
        }
    }

}


