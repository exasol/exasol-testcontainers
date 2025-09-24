package com.exasol.containers.wait.strategy;


import com.exasol.bucketfs.http.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.ContainerLaunchException;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

import static com.exasol.errorreporting.ExaError.messageBuilder;

public class JsonRpcServiceMonitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonRpcServiceMonitor.class);
    private static final Long CONNECTION_TEST_RETRY_INTERVAL_MILLISECONDS = 500L;
    private Duration connectionWaitTimeout = Duration.ofSeconds(250);
    final String URL;

    public JsonRpcServiceMonitor(String URL) {
        this.URL = URL;
    }

     private boolean sendRequest(HttpRequest request) throws URISyntaxException {
         HttpClient httpClient = new HttpClientBuilder().build();
         final Instant before = Instant.now();
         final Instant expiry = before.plus(this.connectionWaitTimeout);
         while (Instant.now().isBefore(expiry)) {
             try {
                 HttpResponse<String> response = httpClient.send(getRequest(), HttpResponse.BodyHandlers.ofString());
                 if (response.statusCode() != 503) {
                     return true;
                 }
             } catch (final IOException exception) { // TODO Error Code
                 LOGGER.warn(messageBuilder("E-ETC-20").message("Unable to execute RPC request {{request}}", request).toString(), exception);
             } catch (final InterruptedException exception) {
                 Thread.currentThread().interrupt();
                 LOGGER.warn(messageBuilder("E-ETC-20").message("Interrupted when sending RPC request {{request}}", request).toString(), exception);
             }
             sleepBeforeNextAttempt();
         }
         return false;

     }

    public boolean isServiceStarted() {

       try{
           HttpRequest request = getRequest();
           return  sendRequest(request);
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
    private void sleepBeforeNextAttempt() {
        try {
            Thread.sleep(CONNECTION_TEST_RETRY_INTERVAL_MILLISECONDS);
        } catch (final InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new ContainerLaunchException("Container start-up wait was interrupted", interruptedException);
        }
    }

}
