package com.ajadhav.deplens.client.maven.mavencentral;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class MavenCentralClient {

    private static final String MAVEN_SEARCH_API = "https://search.maven.org/solrsearch/select";

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String getLatestVersion(String groupId, String artifactId) throws IOException, InterruptedException {
        String query = String.format("g:%s+AND+a:%s",
                URLEncoder.encode(groupId, StandardCharsets.UTF_8),
                URLEncoder.encode(artifactId, StandardCharsets.UTF_8));

        String url = String.format("%s?q=%s&rows=1&wt=json", MAVEN_SEARCH_API, query);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException("Failed to fetch latest version. HTTP status: " + response.statusCode());
            }

            JsonNode root = MAPPER.readTree(response.body());
            JsonNode docs = root.path("response").path("docs");

            if (docs.isArray() && docs.size() > 0) {
                return docs.get(0).path("latestVersion").asString();
            }

            return null;

        } catch (UnknownHostException e) {
            throw new IOException("No internet connection or DNS lookup failed while contacting Maven Central.", e);
        } catch (ConnectException e) {
            throw new IOException("Failed to connect to Maven Central. The service may be unreachable.", e);
        } catch (HttpTimeoutException e) {
            throw new IOException("Request to Maven Central timed out. Please try again later.", e);
        }
    }
}
