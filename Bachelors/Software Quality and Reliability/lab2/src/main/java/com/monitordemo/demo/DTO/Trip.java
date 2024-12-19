package com.monitordemo.demo.DTO;

import lombok.Data;

import java.util.HashMap;
import java.util.LinkedList;


/**
 * Trip is a class to define trips(some trips may consist of 3-4 flights)
 */
@Data
public class Trip {
    String departTime;
    String arrivalTime;
    String origin;
    String destination;
    LinkedList<Flight> flights;
    HashMap<String, Float> classPrices;
    LinkedList<Integer> flightNumbers;

    public Trip(String departTime, String arrivalTime, String origin, String destination, LinkedList<Flight> flights, HashMap<String, Float> classPrices){
        this.departTime = departTime;
        this.arrivalTime = arrivalTime;
        this.origin = origin;
        this.destination = destination;
        this.flights = flights;
        this.classPrices = classPrices;
        flightNumbers = new LinkedList<>();
    }

    public Trip(String departTime, String arrivalTime, String origin, String destination){
        this.departTime = departTime;
        this.arrivalTime = arrivalTime;
        this.origin = origin;
        this.destination = destination;
        this.flights = new LinkedList<Flight>();
        this.classPrices = new HashMap<>();
        flightNumbers = new LinkedList<>();
    }


    public void addPricing(String flight_class, Float price){
        classPrices.putIfAbsent(flight_class,price);
    }
}
