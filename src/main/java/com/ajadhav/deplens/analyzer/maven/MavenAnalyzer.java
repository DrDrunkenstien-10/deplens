package com.ajadhav.deplens.analyzer.maven;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ajadhav.deplens.client.maven.clearlydefined.ClearlyDefinedClient;
import com.ajadhav.deplens.client.maven.mavencentral.MavenCentralClient;
import com.ajadhav.deplens.client.maven.osv.OsvClient;
import com.ajadhav.deplens.dto.AnalysisInfoDTO;
import com.ajadhav.deplens.dto.CveInfoDTO;
import com.ajadhav.deplens.parser.maven.MavenDependency;
import com.ajadhav.deplens.parser.maven.MavenParser;

public class MavenAnalyzer {

    public void analyzeMavenDependencies() throws IOException, InterruptedException {
        MavenParser mavenParser = new MavenParser();
        List<MavenDependency> dependencies = mavenParser.parseMavenDependencies();

        if (dependencies.isEmpty()) {
            System.out.println("No dependencies found in pom.xml.");
            return;
        }

        List<AnalysisInfoDTO> analysisResults = new ArrayList<>();

        System.out.println("\n🔍 Analyzing Maven dependencies...\n");

        for (MavenDependency dependency : dependencies) {
            String groupId = dependency.getGroupId();
            String artifactId = dependency.getArtifactId();
            String currentVersion = dependency.getVersion();

            try {
                System.out.printf("📦 %s:%s (current: %s)%n", groupId, artifactId, currentVersion);

                String latestVersion = MavenCentralClient.getLatestVersion(groupId, artifactId);
                String license = ClearlyDefinedClient.fetchDeclaredLicense(groupId, artifactId, currentVersion);
                List<CveInfoDTO> cveInfoList = OsvClient.fetchVulnerabilities(groupId, artifactId, currentVersion);

                AnalysisInfoDTO info = new AnalysisInfoDTO(
                        artifactId,
                        currentVersion,
                        latestVersion,
                        license,
                        cveInfoList);
                analysisResults.add(info);

                printAnalysisInfo(info);
                System.out.println("------------------------------------------------------------\n");

            } catch (IOException e) {
                System.err.printf("Failed to analyze %s:%s — %s%n", groupId, artifactId, e.getMessage());
                // continue analyzing other dependencies but flag the error
            } catch (Exception e) {
                System.err.printf("Unexpected error while analyzing %s:%s — %s%n", groupId, artifactId,
                        e.getMessage());
                // continue
            }
        }

        System.out.println("Analysis completed for all dependencies.\n");
    }

    private void printAnalysisInfo(AnalysisInfoDTO info) {
        System.out.printf("🔹 Name: %s%n", info.getName());
        System.out.printf("   Current Version: %s%n", info.getCurrentVersion());
        System.out.printf("   Latest Version:  %s%n", info.getLatestVersion());
        System.out.printf("   License:         %s%n", info.getLicense() != null ? info.getLicense() : "Unknown");

        List<CveInfoDTO> cves = info.getCveInfoDTO();
        if (cves == null || cves.isEmpty()) {
            System.out.println("   Vulnerabilities: None found 🎉");
        } else {
            System.out.printf("   Vulnerabilities (%d):%n", cves.size());
            for (CveInfoDTO cve : cves) {
                System.out.printf("      • %s%n", cve.getId() != null ? cve.getId() : "(no ID)");
                if (cve.getSummary() != null && !cve.getSummary().isBlank()) {
                    System.out.printf("        Summary: %s%n", cve.getSummary());
                }
                if (cve.getSeverities() != null && !cve.getSeverities().isEmpty()) {
                    System.out.printf("        Severity: %s%n", String.join(", ", cve.getSeverities()));
                }
                if (cve.getAliases() != null && !cve.getAliases().isEmpty()) {
                    System.out.printf("        Aliases:  %s%n", String.join(", ", cve.getAliases()));
                }
            }
        }
    }
}
