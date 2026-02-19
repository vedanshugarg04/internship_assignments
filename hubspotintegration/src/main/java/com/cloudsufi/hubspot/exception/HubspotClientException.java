package com.cloudsufi.hubspot.exception;

// Thrown when data is bad (400) or ID not found (404)
public class HubspotClientException extends HubspotApiException {
    public HubspotClientException(String message, int statusCode) {
        super("Client Error: " + message, statusCode);
    }
}
