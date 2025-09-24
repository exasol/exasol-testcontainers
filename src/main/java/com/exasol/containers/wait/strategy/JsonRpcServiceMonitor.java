package com.exasol.containers.wait.strategy;


import com.exasol.bucketfs.http.HttpClientBuilder;
import com.exasol.bucketfs.jsonrpc.JsonRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;

import java.net.http.HttpResponse;

import static com.exasol.errorreporting.ExaError.messageBuilder;

public class JsonRpcServiceMonitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonRpcServiceMonitor.class);

    final String URL;

    public JsonRpcServiceMonitor(String URL) {
        this.URL = URL;
    }

     private HttpResponse<String> sendRequest(HttpRequest request) throws URISyntaxException {
         HttpClientBuilder httpClientBuilder = new HttpClientBuilder();
        try {
            return httpClientBuilder.build().send(getRequest(), HttpResponse.BodyHandlers.ofString());
        } catch (final IOException exception) { // TODO Error Code
            throw new JsonRpcException(messageBuilder("E-ETC-20").message("Unable to execute RPC request {{request}}", request).toString(), exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(messageBuilder("E-ETC-20").message("Interrupted when sending RPC request {{request}}", request).toString(), exception);
        }
    }

    public boolean isServiceStarted() {

       try{
           HttpRequest request = getRequest();
           final HttpResponse<String> response = sendRequest(request);
           final String responseBody = response.body();
           LOGGER.info("Received response {} for request {} with body '{}", response, request, responseBody);
           return response.statusCode() != 503; // just checking if the service is up,
       } catch (URISyntaxException e) {
           LOGGER.error("Exception occurred while checking the jrpc service status {}", e.getMessage());
           return false;
       }

    }

    private HttpRequest getRequest() throws URISyntaxException {
        final String requestBody = "{}";
        final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(new URI(URL)).POST(HttpRequest.BodyPublishers.ofString(requestBody));
        final var request = requestBuilder.build();
        LOGGER.info("Sending request {}", request);
        return request;
    }

}
