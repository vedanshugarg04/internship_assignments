package com.cloudsufi.hubspot.exception;

// Thrown when Hubspot server is down (5xx)
public class HubspotServerException extends HubspotApiException {
    public HubspotServerException(String message, int statusCode) {
        super("Server Error: " + message, statusCode);
    }
}
