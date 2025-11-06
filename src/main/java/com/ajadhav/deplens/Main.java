package com.ajadhav.deplens;

import com.ajadhav.deplens.analyzer.maven.MavenAnalyzer;
import com.ajadhav.deplens.exception.LicenseViolationException;
import com.ajadhav.deplens.exception.UnsupportedProjectTypeException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.List;

@Command(name = "deplens", mixinStandardHelpOptions = true, version = "1.0.0", description = "Analyze project dependencies for vulnerabilities and license issues.")
public class Main implements Runnable {

    @Option(names = { "-t", "--type" }, description = "Project type (e.g., maven)", required = true)
    private String projectType;

    @Option(names = {
            "--fail-on-license" }, split = ",", description = "Fail analysis if any dependency has one of the given licenses. Example: --fail-on-license GPL-2.0,LGPL-3.0")
    private List<String> disallowedLicenses;

    @Override
    public void run() {
        try {
            runAnalysis(projectType, disallowedLicenses);
            System.out.println("\nProject analysis completed successfully.");
            System.exit(0);

        } catch (UnsupportedProjectTypeException e) {
            System.err.println(e.getMessage());
            System.exit(2);

        } catch (IOException e) {
            System.err.println("I/O error occurred during analysis: " + e.getMessage());
            System.exit(3);

        } catch (InterruptedException e) {
            System.err.println("Analysis interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
            System.exit(4);

        } catch (LicenseViolationException e) {
            System.err.println("License policy violation detected:");
            e.getViolations().forEach(v -> System.err.printf("   %s:%s — %s%n",
                    v.getName(), v.getCurrentVersion(), v.getLicense()));
            System.exit(5);

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(99);
        }
    }

    public static void runAnalysis(String projectType, List<String> disallowedLicenses)
            throws IOException, InterruptedException, LicenseViolationException {
        switch (projectType.toLowerCase()) {
            case "maven":
                System.out.println("Running analysis for Maven project...\n");
                MavenAnalyzer mavenAnalyzer = new MavenAnalyzer();
                mavenAnalyzer.analyzeMavenDependencies(disallowedLicenses);
                break;
            default:
                throw new UnsupportedProjectTypeException(projectType);
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
