package com.ajadhav.deplens.report;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.ajadhav.deplens.dto.AnalysisInfoDTO;
import com.ajadhav.deplens.dto.CveInfoDTO;

public class ReportGenerator {

    private static final String REPORT_FILE_NAME = "deplens-report.txt";

    public static void generateReport(List<AnalysisInfoDTO> analysisResults) throws IOException {
        if (analysisResults == null || analysisResults.isEmpty()) {
            System.out.println("No analysis results to write into report.");
            return;
        }

        Path reportPath = Path.of(REPORT_FILE_NAME);
        try (BufferedWriter writer = Files.newBufferedWriter(reportPath)) {
            writer.write("Deplens Dependency Analysis Report\n");
            writer.write(
                    "Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.newLine();
            writer.write("============================================================");
            writer.newLine();
            writer.newLine();

            for (AnalysisInfoDTO info : analysisResults) {
                writer.write(String.format("Dependency: %s%n", info.getName()));
                writer.write(String.format("Current Version: %s%n", info.getCurrentVersion()));
                writer.write(String.format("Latest Version:  %s%n", info.getLatestVersion()));
                writer.write(String.format("License:         %s%n",
                        info.getLicense() != null ? info.getLicense() : "Unknown"));

                List<CveInfoDTO> cves = info.getCveInfoDTO();
                if (cves == null || cves.isEmpty()) {
                    writer.write("Vulnerabilities: None found\n");
                } else {
                    writer.write(String.format("Vulnerabilities (%d):%n", cves.size()));
                    for (CveInfoDTO cve : cves) {
                        writer.write(String.format("   • %s%n", cve.getId() != null ? cve.getId() : "(no ID)"));
                        if (cve.getSummary() != null && !cve.getSummary().isBlank()) {
                            writer.write(String.format("     Summary: %s%n", cve.getSummary()));
                        }
                        if (cve.getSeverities() != null && !cve.getSeverities().isEmpty()) {
                            writer.write(String.format("     Severity: %s%n", String.join(", ", cve.getSeverities())));
                        }
                        if (cve.getAliases() != null && !cve.getAliases().isEmpty()) {
                            writer.write(String.format("     Aliases:  %s%n", String.join(", ", cve.getAliases())));
                        }
                    }
                }
                writer.write("------------------------------------------------------------\n");
            }

            writer.write("\nAnalysis completed for all dependencies.\n");
        }

        System.out.printf("Report successfully generated: %s%n", reportPath.toAbsolutePath());
    }
}
