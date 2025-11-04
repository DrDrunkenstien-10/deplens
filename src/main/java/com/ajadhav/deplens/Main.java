package com.ajadhav.deplens;

import com.ajadhav.deplens.analyzer.maven.MavenAnalyzer;
import com.ajadhav.deplens.exception.UnsupportedProjectTypeException;
import java.io.IOException;

public class Main {

    public static void runAnalysis(String projectType) throws IOException, InterruptedException {
        switch (projectType.toLowerCase()) {
            case "maven" -> {
                System.out.println("Running analysis for Maven project...\n");
                MavenAnalyzer mavenAnalyzer = new MavenAnalyzer();
                mavenAnalyzer.analyzeMavenDependencies();
            }

            default -> throw new UnsupportedProjectTypeException(projectType);
        }
    }

    public static void main(String[] args) {
        String projectType = "maven";

        try {
            runAnalysis(projectType);
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

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(99);
        }
    }
}
