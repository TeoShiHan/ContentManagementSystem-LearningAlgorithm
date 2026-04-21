package com.leetcode.learningsystem.service.impl;

import com.leetcode.learningsystem.service.FileTypeRegistry;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class FileTypeRegistryImpl implements FileTypeRegistry {

    private static final Map<String, FileTypeInfo> REGISTRY = new LinkedHashMap<>();

    static {
        // Text/code files - open in VS Code or text editor
        register("txt",        "vscode",          "text/plain",                   true,  "");
        register("md",         "vscode",          "text/markdown",                true,  "# Notes\n\n");
        register("cpp",        "vscode",          "text/x-c++src",                true,  "#include <iostream>\nusing namespace std;\n\nint main() {\n    \n    return 0;\n}\n");
        register("c",          "vscode",          "text/x-csrc",                  true,  "#include <stdio.h>\n\nint main() {\n    \n    return 0;\n}\n");
        register("java",       "vscode",          "text/x-java-source",           true,  "public class Solution {\n    \n}\n");
        register("py",         "vscode",          "text/x-python",                true,  "class Solution:\n    pass\n");
        register("js",         "vscode",          "text/javascript",              true,  "/**\n * @param {number[]} nums\n * @return {number}\n */\nvar solution = function(nums) {\n    \n};\n");
        register("ts",         "vscode",          "text/typescript",              true,  "function solution(nums: number[]): number {\n    \n}\n");
        register("go",         "vscode",          "text/x-go",                    true,  "package main\n\nfunc solution() {\n    \n}\n");
        register("rs",         "vscode",          "text/x-rustsrc",               true,  "fn solution() {\n    \n}\n");

        // Diagram files - open in web apps
        register("excalidraw", "vscode",          "application/json",             true,  "{\n  \"type\": \"excalidraw\",\n  \"version\": 2,\n  \"source\": \"leetcode-learning-system\",\n  \"elements\": [],\n  \"appState\": {\n    \"gridSize\": null,\n    \"viewBackgroundColor\": \"#ffffff\"\n  },\n  \"files\": {}\n}");
        register("drawio",     "web-drawio",      "application/xml",              true,  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<mxfile host=\"app.diagrams.net\">\n  <diagram name=\"Page-1\" id=\"page1\">\n    <mxGraphModel>\n      <root>\n        <mxCell id=\"0\"/>\n        <mxCell id=\"1\" parent=\"0\"/>\n      </root>\n    </mxGraphModel>\n  </diagram>\n</mxfile>");

        // Image files
        register("png",        "browser",         "image/png",                    false, "");
        register("jpg",        "browser",         "image/jpeg",                   false, "");
        register("jpeg",       "browser",         "image/jpeg",                   false, "");
        register("gif",        "browser",         "image/gif",                    false, "");
        register("svg",        "browser",         "image/svg+xml",               true,  "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 100 100\"></svg>");

        // Document files
        register("pdf",        "browser",         "application/pdf",              false, "");
        register("json",       "vscode",          "application/json",             true,  "{\n  \n}\n");
    }

    private static void register(String ext, String openWith, String mimeType, boolean textBased, String template) {
        REGISTRY.put(ext.toLowerCase(), new FileTypeInfo(openWith, mimeType, textBased, template));
    }

    @Override
    public String getOpenWith(String extension) {
        FileTypeInfo info = REGISTRY.get(extension.toLowerCase());
        return info != null ? info.openWith : "browser";
    }

    @Override
    public String getDefaultTemplate(String extension) {
        FileTypeInfo info = REGISTRY.get(extension.toLowerCase());
        return info != null ? info.template : "";
    }

    @Override
    public String getMimeType(String extension) {
        FileTypeInfo info = REGISTRY.get(extension.toLowerCase());
        return info != null ? info.mimeType : "application/octet-stream";
    }

    @Override
    public boolean isTextBased(String extension) {
        FileTypeInfo info = REGISTRY.get(extension.toLowerCase());
        return info != null && info.textBased;
    }

    @Override
    public Map<String, String> getSupportedTypes() {
        Map<String, String> result = new LinkedHashMap<>();
        REGISTRY.forEach((ext, info) -> result.put(ext, info.openWith));
        return result;
    }

    private record FileTypeInfo(String openWith, String mimeType, boolean textBased, String template) {}
}
