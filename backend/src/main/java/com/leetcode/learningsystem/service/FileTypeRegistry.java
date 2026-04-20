package com.leetcode.learningsystem.service;

import java.util.Map;

/**
 * Strategy for determining how to open different file types.
 * Easily extensible - just add new entries to the registry.
 */
public interface FileTypeRegistry {
    String getOpenWith(String extension);
    String getDefaultTemplate(String extension);
    String getMimeType(String extension);
    boolean isTextBased(String extension);
    Map<String, String> getSupportedTypes();
}
