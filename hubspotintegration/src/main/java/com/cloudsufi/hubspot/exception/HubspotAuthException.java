package com.cloudsufi.hubspot.exception;

// Thrown when Token is invalid (401) or Permissions are missing (403)
public class HubspotAuthException extends HubspotApiException {
    public HubspotAuthException(String message, int statusCode) {
        super("Auth Error: " + message, statusCode);
    }
}
