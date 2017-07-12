package com.cipher.sjwsa.examles;

import javax.json.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by a.stopkipny on 11.07.2017.
 */
@SuppressWarnings("Duplicates")
class SimpleOperationHelper {

    private final String serviceBaseUrl;

    public SimpleOperationHelper(String aServeiceBaseUrl) {
        serviceBaseUrl = aServeiceBaseUrl;
    }

    private Collection<String> getFailureDescriptions(Response aResponse) throws IOException{
        List<String> descriptions = new ArrayList<>();
        if (aResponse.getMediaType().equals(MediaType.APPLICATION_JSON_TYPE)) {
            try (InputStream is = aResponse.readEntity(InputStream.class)) {
                JsonReader jr = Json.createReader(is);
                JsonObject responseJson = jr.readObject();
                if (responseJson.containsKey("message")) {
                    descriptions.add(responseJson.getString("message"));
                }
                if (responseJson.containsKey("failureCause")) {
                    descriptions.add(responseJson.getString("failureCause"));
                }
            }
        }
        return descriptions;
    }


    public String createTicket() throws SignerServerInteractionException, IOException {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(serviceBaseUrl).path("ticket");
        Response response = target.request().post(Entity.text(""));
        if (response.getStatus() == 200) {
            try (InputStream is = response.readEntity(InputStream.class)) {
                JsonReader jr = Json.createReader(is);
                JsonObject responseJson = jr.readObject();
                return responseJson.getString("ticketUuid");
            }
        } else {
            final String EXCEPTION_MESSAGE = "Ошибка при создании сессии. ";
            String extendedFailureDescription = getFailureDescriptions(response).stream().collect(Collectors.joining(" "));
            throw new SignerServerInteractionException(EXCEPTION_MESSAGE + extendedFailureDescription);
        }
    }

    public void deleteTicket(String aTicketUuid) throws SignerServerInteractionException, IOException {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(serviceBaseUrl).path("ticket");
        Response response = target.path(aTicketUuid).request().delete();
        if (response.getStatus() != 200) {
            final String EXCEPTION_MESSAGE = "Ошибка при удалении сессии. ";
            String extendedFailureDescription = getFailureDescriptions(response).stream().collect(Collectors.joining(" "));
            throw new SignerServerInteractionException(EXCEPTION_MESSAGE + extendedFailureDescription);
        }
    }


    public void uploadTextData(String aData, String aTicketUuid) throws SignerServerInteractionException, IOException {
        String base64Data = Base64.getEncoder().encodeToString(aData.getBytes());
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("base64Data", base64Data);
        String jsonString = job.build().toString();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(serviceBaseUrl).path("ticket").path(aTicketUuid).path("data");
        Response response = target.request().post(Entity.entity(jsonString, MediaType.APPLICATION_JSON_TYPE));
        if (response.getStatus() != 200) {
            final String EXCEPTION_MESSAGE = "Ошибка при загрузке данных сессии. ";
            String extendedFailureDescription = getFailureDescriptions(response).stream().collect(Collectors.joining(" "));
            throw new SignerServerInteractionException(EXCEPTION_MESSAGE + extendedFailureDescription);
        }
    }

    public void setOptions(Map<String, String> aOptions, String aTicketUuid) throws SignerServerInteractionException, IOException {
        final JsonObjectBuilder job = Json.createObjectBuilder();
        aOptions.entrySet().stream()
                .forEach(e -> job.add(e.getKey(), e.getValue()));
        String jsonString = job.build().toString();
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(serviceBaseUrl).path("ticket").path(aTicketUuid).path("option");
        Response response = target.request().put(Entity.entity(jsonString, MediaType.APPLICATION_JSON_TYPE));
        if (response.getStatus() != 200) {
            final String EXCEPTION_MESSAGE = "Ошибка при установке параметров сессии. ";
            String extendedFailureDescription = getFailureDescriptions(response).stream().collect(Collectors.joining(" "));
            throw new SignerServerInteractionException(EXCEPTION_MESSAGE + extendedFailureDescription);
        }
    }

    public void createDigitalSign(String aTicketUuid) throws SignerServerInteractionException, IOException {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(serviceBaseUrl).path("ticket").path(aTicketUuid).path("ds").path("creator");
        Response response = target.request().post(Entity.text(""));
        if (response.getStatus() != 200) {
            final String EXCEPTION_MESSAGE = "Ошибка при создании ЭЦП. ";
            String extendedFailureDescription = getFailureDescriptions(response).stream().collect(Collectors.joining(" "));
            throw new SignerServerInteractionException(EXCEPTION_MESSAGE + extendedFailureDescription);
        }
    }

    public String getBase64DigitalSign(String aTicketUuid) throws SignerServerInteractionException, IOException {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(serviceBaseUrl).path("ticket").path(aTicketUuid).path("ds").path("base64Data");
        Response response = target.request().get();
        if (response.getStatus() == 200) {
            try (InputStream is = response.readEntity(InputStream.class)) {
                JsonReader jr = Json.createReader(is);
                JsonObject responseJson = jr.readObject();
                return responseJson.getString("base64Data");
            }
        } else {
            final String EXCEPTION_MESSAGE = "Ошибка при получении ЭЦП. ";
            String extendedFailureDescription = getFailureDescriptions(response).stream().collect(Collectors.joining(" "));
            throw new SignerServerInteractionException(EXCEPTION_MESSAGE + extendedFailureDescription);
        }
    }

    public void uploadDsBase64Data(String aBase64Data, String aTicketUuid) throws SignerServerInteractionException, IOException {
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("base64Data", aBase64Data);
        String jsonString = job.build().toString();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(serviceBaseUrl).path("ticket").path(aTicketUuid).path("ds").path("data");
        Response response = target.request().post(Entity.entity(jsonString, MediaType.APPLICATION_JSON_TYPE));
        if (response.getStatus() != 200) {
            final String EXCEPTION_MESSAGE = "Ошибка при загрузке данных ЭЦП. ";
            String extendedFailureDescription = getFailureDescriptions(response).stream().collect(Collectors.joining(" "));
            throw new SignerServerInteractionException(EXCEPTION_MESSAGE + extendedFailureDescription);
        }
    }

    public void verifyDigitalSign(String aTicketUuid) throws SignerServerInteractionException, IOException {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(serviceBaseUrl).path("ticket").path(aTicketUuid).path("ds").path("verifier");
        Response response = target.request().post(Entity.text(""));
        if (response.getStatus() != 200) {
            final String EXCEPTION_MESSAGE = "Ошибка при проверке ЭЦП. ";
            String extendedFailureDescription = getFailureDescriptions(response).stream().collect(Collectors.joining(" "));
            throw new SignerServerInteractionException(EXCEPTION_MESSAGE + extendedFailureDescription);
        }
    }

    public JsonArray getDigitalSignVerifyingResult(String aTicketUuid) throws SignerServerInteractionException, IOException {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(serviceBaseUrl).path("ticket").path(aTicketUuid).path("ds").path("verifier");
        Response response = target.request().get();
        if (response.getStatus() == 200) {
            try (InputStream is = response.readEntity(InputStream.class)) {
                JsonReader jr = Json.createReader(is);
                JsonObject responseJson = jr.readObject();
                return responseJson.getJsonArray("verifyResults");
            }
        } else {
            final String EXCEPTION_MESSAGE = "Ошибка при получении результа проверки ЭЦП. ";
            String extendedFailureDescription = getFailureDescriptions(response).stream().collect(Collectors.joining(" "));
            throw new SignerServerInteractionException(EXCEPTION_MESSAGE + extendedFailureDescription);
        }
    }

}
