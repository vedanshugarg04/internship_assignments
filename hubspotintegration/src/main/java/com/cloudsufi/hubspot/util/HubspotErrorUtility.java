package com.cloudsufi.hubspot.util;

import com.cloudsufi.hubspot.exception.HubspotApiException;
import com.cloudsufi.hubspot.exception.HubspotAuthException;
import com.cloudsufi.hubspot.exception.HubspotClientException;
import com.cloudsufi.hubspot.exception.HubspotServerException;
import com.google.api.client.http.HttpResponseException;
import com.google.common.flogger.FluentLogger;

public class HubspotErrorUtility {
    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();

    public static void handleHubspotError(HttpResponseException e) {
        int code = e.getStatusCode();
        String msg = e.getStatusMessage();

        LOGGER.atSevere().log("HubSpot API Error [%d]: %s", code, e.getContent());

        switch (code) {
            case 401, 403 -> throw new HubspotAuthException("Invalid Token or Scopes.", code);
            case 409 -> throw new HubspotClientException("Contact already exists! (Duplicate Email)", code);
            case 429 -> throw new HubspotServerException("Rate Limit Hit!", code);

            default -> {
                if (code >= 500) {
                    throw new HubspotServerException("HubSpot is down. Try again later.", code);
                } else if (code >= 400) {
                    throw new HubspotClientException("Bad Request: " + msg, code);
                } else {
                    throw new HubspotApiException("Unknown Error: " + msg, code);
                }
            }
        }
    }
}
