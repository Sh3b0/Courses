package com.monitordemo.demo.DAO;

import com.monitordemo.demo.DTO.Flight;
import com.monitordemo.demo.DTO.Trip;
import com.monitordemo.demo.DTO.ValidationResponse;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * This is class to verify trips
 */
public class DataValidator {
    /**
     * validate function is vefifying flights
     * @param airlineFlights this is list of flights from airlines
     * @param tripActionsFlights this is list of flights from TripActions website
     * @return analyzed metrics
     */
    public ValidationResponse validate(LinkedList<Trip> airlineFlights, LinkedList<Trip> tripActionsFlights){

        LinkedList<Trip> correspondingTrips = new LinkedList<>();
        int Wp = 0; // Number of flights with wrong prices
        int Wd = 0; // Number of flights with wrong departure time
        int Wa = 0; // Number of flights with wrong arrival time
        int W = 0; // Number of wrong flights
        int M = 0; // Number of missed flights
        int NF = 0; // Number of flights, not found on airlines website
        float V = 0;  // Mark of data validness
        int N = tripActionsFlights.size(); // Number of trips
        boolean tripWasSeen;
        for (int i = 0;i<tripActionsFlights.size();i++) {
            tripWasSeen = false;
            for (int j = 0;j<airlineFlights.size() && !tripWasSeen;j++){
                if(tripActionsFlights.get(i).getFlightNumbers().equals(airlineFlights.get(j).getFlightNumbers())){
                    tripWasSeen = true;
                    if(tripActionsFlights.get(i).getArrivalTime().equals(airlineFlights.get(j).getArrivalTime()) &&
                            tripActionsFlights.get(i).getDepartTime().equals(airlineFlights.get(j).getDepartTime())){
                            correspondingTrips.add(tripActionsFlights.get(i));

                    } else {
                        if (!tripActionsFlights.get(i).getArrivalTime().equals(airlineFlights.get(j).getArrivalTime())) {
                          Wa++;
                        }
                        if (!tripActionsFlights.get(i).getDepartTime().equals(airlineFlights.get(j).getDepartTime())) {
                          Wd++;
                        }
                        for(int a = 0;i<tripActionsFlights.get(i).getFlights().size();i++){
                            if(!tripActionsFlights.get(i).getFlights().get(a).getArrivalTime().equals(airlineFlights.get(i).getFlights().get(a).getArrivalTime())){
                                Wa++;
                            }
                            if(!tripActionsFlights.get(i).getFlights().get(a).getDepartTime().equals(airlineFlights.get(i).getFlights().get(a).getDepartTime())){
                                Wd++;
                            }
                            for(String b:tripActionsFlights.get(i).getClassPrices().keySet()){
                                if(!tripActionsFlights.get(i).getClassPrices().get(b).equals(airlineFlights.get(i).getClassPrices().get(b))){
                                    Wp++;
                                }
                            }
                        }
                        W++;

                    }
                    tripActionsFlights.remove(i);
                    airlineFlights.remove(j);
                    i-=1;
                }
            }
            if (!tripWasSeen) {
                NF++;
            }
        }
        M = airlineFlights.size();
        float corrPrices = (float)(N - Wp) /N * 100;
        float corrDepart = (float)(N-Wd) / N * 100;
        float corrArrival = (float)(N-Wa) / N * 100;
        V = ( corrPrices * 4 + corrDepart * 3 + corrArrival * 3 +(float)(N-W)/N*100*5+(float)(N-NF)/N*100*2 + (float)N/(N+M)*100*3)/20;
        return new ValidationResponse(corrPrices,corrDepart,corrArrival,W,M,NF,V,N);
    }


}
