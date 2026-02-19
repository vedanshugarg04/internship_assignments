package com.cloudsufi.hubspot.config;

public class HubspotConfig {
    public static final String URL_CREATE_CONTACT = "https://api.hubapi.com/crm/v3/objects/contacts";
    public static final String URL_GET_CONTACT_BASE = "https://api.hubapi.com/crm/v3/objects/contacts/";

    public static String getAccessToken() {
        String token = System.getenv("HUBSPOT_ACCESS_TOKEN");

        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Access Token is not set");
        }

        return token;
    }
}
