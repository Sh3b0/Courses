package com.monitordemo.demo.DTO;

import lombok.Data;

import java.util.HashMap;

/**
 * Flight class is class for parsed flights
 * @author BritikovKI
 * @version 0.1
 */
@Data
public class Flight {
    String departTime;
    String arrivalTime;
    String ticketNumber;
    String flightCode;
    String plane;
    String origin;
    String destination;


    public Flight(String departTime, String arrivalTime, String ticketNumber, String flightCode, String plane, String origin, String destination){
        this.departTime = departTime;
        this.arrivalTime = arrivalTime;
        this.ticketNumber = ticketNumber;
        this.flightCode = flightCode;
        this.plane = plane;
        this.origin = origin;
        this.destination = destination;
    }


}
