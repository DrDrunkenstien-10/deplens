package com.ajadhav.deplens.exception;

public class UnsupportedProjectTypeException extends RuntimeException {
    public UnsupportedProjectTypeException(String projectType) {
        super("Unsupported project type: " + projectType);
    }
}
