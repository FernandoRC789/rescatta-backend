package com.rescatta.backend.report.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rescatta.reports")
public record ReportProperties(int maxPhotos) {
}
