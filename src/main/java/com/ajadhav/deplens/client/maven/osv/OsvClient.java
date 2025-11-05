package com.ajadhav.deplens.client.maven.osv;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.ajadhav.deplens.dto.CveInfoDTO;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class OsvClient {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static List<CveInfoDTO> fetchVulnerabilities(String groupId, String artifactId, String version)
            throws IOException, InterruptedException {

        String packageName = groupId + ":" + artifactId;

        String requestBody = String.format("""
                {
                  "package": {
                    "ecosystem": "Maven",
                    "name": "%s"
                  },
                  "version": "%s"
                }
                """, packageName, version);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.osv.dev/v1/query"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            int code = response.statusCode();

            if (code != 200) {
                throw new IOException("OSV.dev API returned HTTP " + code + ": " + response.body());
            }

            JsonNode root = MAPPER.readTree(response.body());
            JsonNode vulns = root.path("vulns");

            List<CveInfoDTO> results = new ArrayList<>();
            if (vulns.isArray()) {
                for (JsonNode v : vulns) {
                    String id = v.path("id").asString(null);
                    String summary = v.path("summary").asString(null);

                    List<String> aliases = new ArrayList<>();
                    if (v.has("aliases") && v.get("aliases").isArray()) {
                        for (JsonNode alias : v.get("aliases")) {
                            aliases.add(alias.asString());
                        }
                    }

                    // severity
                    List<String> severities = new ArrayList<>();
                    JsonNode dbSpec = v.path("database_specific");
                    if (dbSpec.isObject()) {
                        JsonNode dbSeverity = dbSpec.path("severity");
                        if (!dbSeverity.isMissingNode() && !dbSeverity.isNull()) {
                            String human = dbSeverity.asString();
                            if (!human.isBlank() && !severities.contains(human)) {
                                severities.add(human);
                            }
                        }
                    }

                    results.add(new CveInfoDTO(id, summary, aliases, severities));
                }
            }

            return results;

        } catch (UnknownHostException e) {
            throw new IOException("No internet connection or DNS lookup failed while contacting OSV.dev.", e);
        } catch (ConnectException e) {
            throw new IOException("Failed to connect to OSV.dev API. The service may be unreachable.", e);
        } catch (HttpTimeoutException e) {
            throw new IOException("Request to OSV.dev API timed out. Please try again later.", e);
        }
    }
}
