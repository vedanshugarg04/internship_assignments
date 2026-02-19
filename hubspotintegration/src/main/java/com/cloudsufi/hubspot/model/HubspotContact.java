package com.cloudsufi.hubspot.model;

import com.google.api.client.util.Key;

public class HubspotContact {

    @Key
    private String id;

    @Key
    private ContactProperties properties;

    public String getId() {
        return id;
    }
    public String getEmail() {
        return properties != null ? properties.email : null;
    }
    public String getFirstName() {
        return properties != null ? properties.firstname : null;
    }
    public String getLastName() {
        return properties != null ? properties.lastname : null;
    }

    // For Testing purposes
    public void setId(String id) {
        this.id = id;
    }
    public void setEmail(String email) {
        ensureProperties();
        this.properties.email = email;
    }
    public void setFirstName(String firstname) {
        ensureProperties();
        this.properties.firstname = firstname;
    }
    public void setLastName(String lastname) {
        ensureProperties();
        this.properties.lastname = lastname;
    }
    private void ensureProperties() {
        if (this.properties == null) {
            this.properties = new ContactProperties();
        }
    }

    public static class ContactProperties {
        @Key
        public String email;

        @Key
        public String firstname;

        @Key
        public String lastname;
    }
}
