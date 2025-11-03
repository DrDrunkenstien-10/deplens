package com.ajadhav.deplens.analyzer.maven;

import java.io.IOException;
import java.util.List;

import com.ajadhav.deplens.parser.maven.MavenDependency;
import com.ajadhav.deplens.parser.maven.MavenParser;

public class MavenAnalyzer {
    public void analyzeMavenDependencies() {
        MavenParser mavenParser = new MavenParser();

        try {
            List<MavenDependency> dependencies = mavenParser.parseMavenDependencies();

            if (dependencies.isEmpty()) {
                System.out.println("No dependencies found in pom.xml.");
                System.exit(0);
            }

            for (MavenDependency dependency : dependencies) {
                System.out.println(dependency.getArtifactId() + " " + dependency.getGroupId() + " "
                        + dependency.getVersion());
            }
        } catch (IOException e) {
            System.err.println("Error reading or parsing pom.xml: " + e.getMessage());
            System.exit(1);
        }
    }
}
