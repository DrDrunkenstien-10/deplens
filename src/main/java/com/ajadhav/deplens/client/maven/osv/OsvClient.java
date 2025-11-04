package com.ajadhav.deplens.client.maven.osv;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.ajadhav.deplens.dto.CveInfoDTO;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class OsvClient {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static List<CveInfoDTO> fetchVulnerabilities(String groupId, String artifactId, String version)
            throws IOException, InterruptedException {

        // OSV expects package.name = "groupId:artifactId"
        String packageName = groupId + ":" + artifactId;

        // Build request JSON body
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
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

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

                // aliases
                List<String> aliases = new ArrayList<>();
                if (v.has("aliases") && v.get("aliases").isArray()) {
                    for (JsonNode alias : v.get("aliases")) {
                        aliases.add(alias.asString());
                    }
                }

                // severity - robust handling:
                List<String> severities = new ArrayList<>();

                // 2) database_specific.severity (human readable e.g., "HIGH") - add if present
                // and not duplicate
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
    }
}
