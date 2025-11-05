package com.ajadhav.deplens.client.maven.depsdev;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class DepsDevClient {

    private static final String BASE_URL = "https://api.deps.dev/v3/systems/maven/packages";

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String fetchLicense(String groupId, String artifactId, String version)
            throws IOException, InterruptedException {

        String packageName = URLEncoder.encode(groupId + ":" + artifactId, StandardCharsets.UTF_8);
        String versionEncoded = URLEncoder.encode(version, StandardCharsets.UTF_8);
        String url = String.format("%s/%s/versions/%s", BASE_URL, packageName, versionEncoded);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            int code = response.statusCode();

            if (code == 200) {
                JsonNode root = MAPPER.readTree(response.body());
                JsonNode licenses = root.path("licenses");

                if (licenses.isArray() && licenses.size() > 0) {
                    String licenseName = licenses.get(0).asString(null);
                    return (licenseName != null && !licenseName.isEmpty()) ? licenseName : null;
                }
                
                return null;

            } else if (code == 404) {
                return null;
            } else {
                throw new IOException("deps.dev API returned HTTP " + code + ": " + response.body());
            }

        } catch (UnknownHostException e) {
            throw new IOException("No internet connection or DNS lookup failed while contacting deps.dev.", e);
        } catch (ConnectException e) {
            throw new IOException("Failed to connect to deps.dev API. The service may be unreachable.", e);
        } catch (HttpTimeoutException e) {
            throw new IOException("Request to deps.dev API timed out. Please try again later.", e);
        }
    }
}
