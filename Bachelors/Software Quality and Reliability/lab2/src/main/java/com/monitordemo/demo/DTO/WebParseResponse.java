package com.monitordemo.demo.DTO;


import lombok.Data;

import java.util.LinkedList;

@Data
/** WebParseResponse is response to search for flights
 * @version 0.1
 */
public class WebParseResponse {
    private String website;
    private String from;
    private String to;
    private String departDate;
    private String returnDate;
    private LinkedList<Trip> trips;

    public WebParseResponse(WebsiteParceRequest req, LinkedList<Trip> flights){
        this.trips = flights;
        this.website = req.getWebsite();
        this.from = req.getFrom();
        this.to = req.getTo();
        this.departDate = req.getDepartDate();
        this.returnDate = req.getReturnDate();
    }
}
