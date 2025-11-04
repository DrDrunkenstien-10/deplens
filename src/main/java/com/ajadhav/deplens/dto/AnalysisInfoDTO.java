package com.ajadhav.deplens.dto;

import java.util.List;

public class AnalysisInfoDTO {
    private String name;
    private String currentVersion;
    private String latestVersion;
    private String license;
    private List<CveInfoDTO> CveInfoDTO;

    // Parameterized constructor
    public AnalysisInfoDTO(String name, String currentVersion, String latestVersion, String license,
            List<com.ajadhav.deplens.dto.CveInfoDTO> cveInfoDTO) {
        this.name = name;
        this.currentVersion = currentVersion;
        this.latestVersion = latestVersion;
        this.license = license;
        CveInfoDTO = cveInfoDTO;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public List<CveInfoDTO> getCveInfoDTO() {
        return CveInfoDTO;
    }

    public void setCveInfoDTO(List<CveInfoDTO> cveInfoDTO) {
        CveInfoDTO = cveInfoDTO;
    }
}
