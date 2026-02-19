package com.cloudsufi.hubspot.model;

import com.google.api.client.util.Key;

public class ContactData {
    @Key
    public String email;

    @Key
    public String firstname;

    @Key
    public String lastname;

    public ContactData(String email, String firstname, String lastname) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
    }
}
