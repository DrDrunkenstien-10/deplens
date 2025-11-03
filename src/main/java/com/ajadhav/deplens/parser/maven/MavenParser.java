package com.ajadhav.deplens.parser.maven;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class MavenParser {
    private static final String DEFAULT_POM_NAME = "pom.xml";

    public List<MavenDependency> parseMavenDependencies() throws IOException {
        // Automatically detect pom.xml in current working directory
        File pomFile = new File(System.getProperty("user.dir"), DEFAULT_POM_NAME);

        if (!pomFile.exists() || !pomFile.isFile()) {
            throw new IOException("pom.xml not found in current directory: " + pomFile.getAbsolutePath());
        }

        List<MavenDependency> dependenciesList = new ArrayList<>();
        MavenXpp3Reader reader = new MavenXpp3Reader();

        try (FileReader fileReader = new FileReader(pomFile)) {
            Model model = reader.read(fileReader);

            // Extract dependency information
            for (Dependency dep : model.getDependencies()) {
                dependenciesList.add(new MavenDependency(
                        dep.getGroupId(),
                        dep.getArtifactId(),
                        dep.getVersion()));
            }

        } catch (XmlPullParserException e) {
            throw new IOException("Failed to parse pom.xml: " + e.getMessage(), e);
        }

        return dependenciesList;
    }
}
