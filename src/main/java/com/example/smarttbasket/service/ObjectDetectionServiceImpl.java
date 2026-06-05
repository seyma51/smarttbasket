package com.example.smarttbasket.service;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ObjectDetectionServiceImpl implements ObjectDetectionService {

    private double lastConfidenceScore = 0.0;

    @Override
    public String detectItem(File imageFile) {
        try {
            if (imageFile == null) {
                return "Bilinmeyen Urun";
            }
            Image img = ImageFactory.getInstance().fromFile(imageFile.toPath());

            Criteria<Image, DetectedObjects> criteria = Criteria.builder()
                    .setTypes(Image.class, DetectedObjects.class)
                    .optArtifactId("yolov8n")
                    .build();

            try (ZooModel<Image, DetectedObjects> model = criteria.loadModel();
                 Predictor<Image, DetectedObjects> predictor = model.newPredictor()) {

                DetectedObjects results = predictor.predict(img);

                if (!results.items().isEmpty()) {
                    var bestMatch = results.items().get(0);
                    this.lastConfidenceScore = bestMatch.getProbability();
                    return bestMatch.getClassName();
                }
            }
        } catch (Exception e) {
            System.err.println("Yapay zeka modeli calisirken hata olustu: " + e.getMessage());
        }

        this.lastConfidenceScore = 0.0;
        return "Bilinmeyen Urun";
    }

    @Override
    public double getConfidenceScore() {
        return this.lastConfidenceScore;
    }
}