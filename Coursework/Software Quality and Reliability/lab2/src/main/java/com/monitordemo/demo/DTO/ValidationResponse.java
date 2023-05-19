package com.monitordemo.demo.DTO;

import lombok.Data;

/**
 * this is class for json response with validation metrics
 * Cp - correctness of prices
 * Cd - correctness of departure time
 * Ca - correctness of arrival time
 * W - wrong flights
 * M - missed flights
 * V - data correctness grade (A-E)
 * N - number of flights on website
 */
@Data
public class ValidationResponse {
    Float Cp;
    Float Cd;
    Float Ca;
    Integer W;
    Integer M;
    Integer Nf;
    Float V;
    Integer N;

    public ValidationResponse(Float Cp,Float Cd, Float Ca, Integer W, Integer M, Integer Nf, Float V, Integer N){
        this.Cp = Cp;
        this.Cd = Cd;
        this.Ca = Ca;
        this.W = W;
        this.M = M;
        this.Nf = Nf;
        this.V = V;
        this.N = N;

    }
}
