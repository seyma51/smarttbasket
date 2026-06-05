package com.example.smarttbasket.service;

import java.io.File;

public interface ObjectDetectionService {
    String detectItem(File imageFile);
    double getConfidenceScore();
}