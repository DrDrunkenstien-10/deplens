package com.ajadhav.deplens.dto;

import java.util.List;

public class CveInfoDTO {
    private String id;
    private String summary;
    private List<String> aliases;
    private List<String> severities;

    // Parameterized constructor
    public CveInfoDTO(String id, String summary, List<String> aliases, List<String> severities) {
        this.id = id;
        this.summary = summary;
        this.aliases = aliases;
        this.severities = severities;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public List<String> getSeverities() {
        return severities;
    }

    public void setSeverities(List<String> severities) {
        this.severities = severities;
    }
}