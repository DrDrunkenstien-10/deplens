package com.ajadhav.deplens.client.maven.clearlydefined;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class ClearlyDefinedClient {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String fetchDeclaredLicense(String groupId, String artifactId, String version)
            throws IOException, InterruptedException {

        // Build the coordinate: namespace/name must be URL-encoded (groupId + "/" +
        // artifactId)
        String namespaceAndName = URLEncoder.encode(groupId + "/" + artifactId, StandardCharsets.UTF_8);
        String url = String.format("https://api.clearlydefined.io/definitions/maven/mavencentral/%s/%s",
                namespaceAndName, URLEncoder.encode(version, StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("Accept-Version", "1.0.0")
                .GET()
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        int code = response.statusCode();
        String body = response.body();

        if (code == 200) {
            JsonNode root = MAPPER.readTree(body);
            JsonNode declared = root.path("licensed").path("declared");
            if (declared.isMissingNode() || declared.isNull()) {
                return null; // license not present in JSON
            }
            return declared.asString();
        } else if (code == 404) {
            // Not found in ClearlyDefined
            return null;
        } else {
            // Other HTTP errors - bubble up useful info
            throw new IOException("ClearlyDefined API returned HTTP " + code + ": " + body);
        }
    }

    public static void main(String[] args) {
        try {
            String license = fetchDeclaredLicense("org.apache.commons", "commons-lang3", "3.12.0");
            System.out.println("Declared license: " + (license != null ? license : "N/A"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
