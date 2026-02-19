package com.cloudsufi.hubspot.service;

import com.cloudsufi.hubspot.config.HubspotConfig;
import com.cloudsufi.hubspot.model.HubspotContact;
import com.cloudsufi.hubspot.model.ContactData;
import com.cloudsufi.hubspot.model.HubspotRequest;
import com.cloudsufi.hubspot.util.HubspotErrorUtility;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.common.flogger.FluentLogger;

import java.io.IOException;

public class HubspotService {

    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new GsonFactory();
    private final HttpRequestFactory requestFactory;

    public HubspotService() {
        this.requestFactory = HTTP_TRANSPORT.createRequestFactory(request -> {
            request.setParser(new JsonObjectParser(JSON_FACTORY));
            request.setHeaders(new HttpHeaders()
                    .setAuthorization("Bearer " + HubspotConfig.getAccessToken()));
            request.setParser(JSON_FACTORY.createJsonObjectParser());
        });
    }

    // A constructor specifically for testing
    public HubspotService(HttpRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    public HubspotContact createContact(String email, String firstName, String lastName) {
        try {
            ContactData contact = new ContactData(email, firstName, lastName);
            HubspotRequest payload = new HubspotRequest(contact);

            GenericUrl url = new GenericUrl(HubspotConfig.URL_CREATE_CONTACT);
            
            HttpContent content = new JsonHttpContent(JSON_FACTORY, payload);

            HttpRequest request = requestFactory.buildPostRequest(url, content);

            HttpResponse response = request.execute();

            return response.parseAs(HubspotContact.class);
        } catch (HttpResponseException e) {
            HubspotErrorUtility.handleHubspotError(e);
            return null;
        } catch (IOException e) {
            LOGGER.atSevere().withCause(e).log("Network failure while creating contact");
            throw new RuntimeException("Network Error", e);
        }
    }

    public HubspotContact getContact(String email) {
        try {

            String fullUrlString = HubspotConfig.URL_GET_CONTACT_BASE + email;

            GenericUrl url = new GenericUrl(fullUrlString);
            url.put("idProperty", "email"); // ?idProperty=email

            LOGGER.atFine().log("Generated URL: %s", url.build());

            HttpRequest request = requestFactory.buildGetRequest(url);

            HttpResponse response = request.execute();

            return response.parseAs(HubspotContact.class);
        } catch (HttpResponseException e) {
            HubspotErrorUtility.handleHubspotError(e);
            return null;
        } catch (IOException e) {
            LOGGER.atSevere().withCause(e).log("Network failure while fetching contact");
            throw new RuntimeException("Network error fetching contacts", e);
        }
    }
}
