package com.cloudsufi.hubspot.client;

import com.cloudsufi.hubspot.exception.HubspotAuthException;
import com.cloudsufi.hubspot.exception.HubspotClientException;
import com.cloudsufi.hubspot.model.HubspotContact;
import com.cloudsufi.hubspot.service.HubspotService;
import com.google.common.flogger.FluentLogger;

public class HubspotClient {
    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();
    private static final HubspotService hubspotService = new HubspotService();

    private static final String EMAIL = "ethanhunt123@hubspot.com";
    private static final String firstName = "Ethan";
    private static final String lastName = "Hunt";

    private static void createContact() {
        LOGGER.atInfo().log("Creating Contact...");

        try {
            HubspotContact newContact = hubspotService.createContact(EMAIL, firstName, lastName);

            LOGGER.atInfo().log("Success! Contact Created.");
            LOGGER.atInfo().log("ID: %s", newContact.getId());
            LOGGER.atInfo().log("Name: %s %s", newContact.getFirstName(), newContact.getLastName());
            LOGGER.atInfo().log("Email: %s", newContact.getEmail());

        } catch (HubspotClientException e) {
            LOGGER.atWarning().log("Action Failed: %s", e.getMessage());
        } catch (HubspotAuthException e) {
            LOGGER.atSevere().log("Security Alert: %s", e.getMessage());
        } catch (Exception e) {
            LOGGER.atSevere().withCause(e).log("Creation Failed");
        }
    }

    private static void getContact() {
        LOGGER.atInfo().log("Fetching Contact...");

        try {
            HubspotContact contact = hubspotService.getContact(EMAIL);

            LOGGER.atInfo().log("Success! Data Received:");
            LOGGER.atInfo().log("ID: %s", contact.getId());
            LOGGER.atInfo().log("Name: %s %s", contact.getFirstName(), contact.getLastName());
            LOGGER.atInfo().log("Email: %s", contact.getEmail());

        } catch (HubspotClientException e) {
            LOGGER.atWarning().log("Contact Not Found: %s", e.getMessage());
        } catch (Exception e) {
            LOGGER.atSevere().withCause(e).log("Fetch Failed");
        }
    }

    public static void main(String[] args) {
        createContact();
        LOGGER.atInfo().log("\n--------------------------------------\n");
        getContact();
    }
}
