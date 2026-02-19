package com.cloudsufi.hubspot;

import com.cloudsufi.hubspot.exception.HubspotAuthException;
import com.cloudsufi.hubspot.exception.HubspotClientException;
import com.cloudsufi.hubspot.model.HubspotContact;
import com.cloudsufi.hubspot.service.HubspotService;
import com.google.api.client.http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HubspotServiceTest {

    @Mock
    private HttpRequestFactory requestFactory;

    @Mock
    private HttpRequest request;

    @Mock
    private HttpResponse response;

    private HubspotService service;

    @BeforeEach
    void setUp() {
        service = new HubspotService(requestFactory);

        HttpHeaders headers = new HttpHeaders();
        lenient().when(request.getHeaders()).thenReturn(headers);
    }

    @Test
    void createContact_Success() throws IOException {
        when(requestFactory.buildPostRequest(any(GenericUrl.class), any(HttpContent.class)))
                .thenReturn(request);
        when(request.execute()).thenReturn(response);

        HubspotContact mockContact = new HubspotContact();
        mockContact.setEmail("found@hubspot.com");
        when(response.parseAs(HubspotContact.class)).thenReturn(mockContact);

        HubspotContact result = service.createContact("found@hubspot.com", "Test", "User");

        assertNotNull(result);
        assertEquals("found@hubspot.com", result.getEmail());
        verify(request, times(1)).execute();
    }

    @Test
    void createContact_Duplicate_ThrowsClientException() throws IOException {
        when(requestFactory.buildPostRequest(any(GenericUrl.class), any(HttpContent.class)))
                .thenReturn(request);

        HttpResponseException conflictError = new HttpResponseException.Builder(409, "Conflict", new HttpHeaders()).build();
        when(request.execute()).thenThrow(conflictError);

        HubspotClientException exception = assertThrows(HubspotClientException.class, () -> {
            service.createContact("duplicate@hubspot.com", "Dup", "User");
        });

        assertTrue(exception.getMessage().contains("Duplicate"));
    }

    @Test
    void createContact_Unauthorized_ThrowsAuthException() throws IOException {
        when(requestFactory.buildPostRequest(any(GenericUrl.class), any(HttpContent.class)))
                .thenReturn(request);

        HttpResponseException authError = new HttpResponseException.Builder(401, "Unauthorized", new HttpHeaders()).build();
        when(request.execute()).thenThrow(authError);

        assertThrows(HubspotAuthException.class, () -> {
            service.createContact("hacker@hubspot.com", "Hacker", "User");
        });
    }

    @Test
    void getContact_Success() throws IOException {
        when(requestFactory.buildGetRequest(any(GenericUrl.class))).thenReturn(request);
        when(request.execute()).thenReturn(response);

        HubspotContact mockContact = new HubspotContact();
        mockContact.setId("101");
        mockContact.setEmail("found@hubspot.com");
        when(response.parseAs(HubspotContact.class)).thenReturn(mockContact);

        HubspotContact result = service.getContact("found@hubspot.com");

        assertNotNull(result);
        assertEquals("101", result.getId());
        assertEquals("found@hubspot.com", result.getEmail());
    }

    @Test
    void getContact_NotFound_ThrowsClientException() throws IOException {
        when(requestFactory.buildGetRequest(any(GenericUrl.class))).thenReturn(request);

        HttpResponseException notFoundError = new HttpResponseException.Builder(404, "Not Found", new HttpHeaders()).build();
        when(request.execute()).thenThrow(notFoundError);

        HubspotClientException exception = assertThrows(HubspotClientException.class, () -> {
            service.getContact("missing@hubspot.com");
        });

        assertNotNull(exception);
    }
}
