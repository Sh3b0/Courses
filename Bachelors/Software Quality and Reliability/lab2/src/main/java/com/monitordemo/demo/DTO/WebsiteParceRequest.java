package com.monitordemo.demo.DTO;



import lombok.Data;


@Data
/**
 *  WebsiteParceRequest is request to search for flights
 * @version 0.1
 */
public class WebsiteParceRequest {
    private String website;
    private String from;
    private String to;
    private String departDate;
    private String returnDate;
}
