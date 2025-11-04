package com.ajadhav.deplens.client.maven.mavencentral;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class MavenCentralClient {

    private static final String MAVEN_SEARCH_API = "https://search.maven.org/solrsearch/select";

    public static String getLatestVersion(String groupId, String artifactId) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        // Build query string safely
        String query = String.format("g:%s+AND+a:%s",
                URLEncoder.encode(groupId, StandardCharsets.UTF_8),
                URLEncoder.encode(artifactId, StandardCharsets.UTF_8));

        String url = String.format("%s?q=%s&rows=1&wt=json", MAVEN_SEARCH_API, query);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch latest version. HTTP status: " + response.statusCode());
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());

        JsonNode docs = root.path("response").path("docs");
        if (docs.isArray() && docs.size() > 0) {
            return docs.get(0).path("latestVersion").asString();
        }

        return null;
    }

    public static void main(String[] args) {
        try {
            String groupId = "org.springframework.boot";
            String artifactId = "spring-boot";

            String latestVersion = getLatestVersion(groupId, artifactId);
            System.out.println("Latest version of " + groupId + ":" + artifactId + " => " + latestVersion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
