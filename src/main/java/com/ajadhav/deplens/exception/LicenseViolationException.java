package com.ajadhav.deplens.exception;

import com.ajadhav.deplens.dto.AnalysisInfoDTO;
import java.util.List;

public class LicenseViolationException extends Exception {
    private final List<AnalysisInfoDTO> violations;

    public LicenseViolationException(List<AnalysisInfoDTO> violations) {
        super("Disallowed license(s) detected.");
        this.violations = violations;
    }

    public List<AnalysisInfoDTO> getViolations() {
        return violations;
    }
}
