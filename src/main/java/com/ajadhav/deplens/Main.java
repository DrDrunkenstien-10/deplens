package com.ajadhav.deplens;

import com.ajadhav.deplens.analyzer.maven.MavenAnalyzer;
import com.ajadhav.deplens.exception.UnsupportedProjectTypeException;

public class Main {
    public static void runAnalysis(String projectType) {
        switch (projectType.toLowerCase()) {
            case "maven":
                System.out.println("Running analysis for Maven project...");

                MavenAnalyzer mavenAnalyzer = new MavenAnalyzer();

                mavenAnalyzer.analyzeMavenDependencies();

                System.exit(0);

                break;

            default:
                throw new UnsupportedProjectTypeException(projectType);
        }
    }

    public static void main(String[] args) {
        String projectType = "maven";

        try {
            runAnalysis(projectType);
        } catch (UnsupportedProjectTypeException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
    }
}