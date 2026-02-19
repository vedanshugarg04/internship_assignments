package com.cloudsufi.hubspot.model;

import com.google.api.client.util.Key;

public class HubspotRequest {
    @Key
    public ContactData properties;

    public HubspotRequest(ContactData data) {
        this.properties = data;
    }
}
