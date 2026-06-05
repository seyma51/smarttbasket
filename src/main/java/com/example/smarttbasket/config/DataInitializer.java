package com.example.smarttbasket.config;

import com.example.smarttbasket.model.Product;
import com.example.smarttbasket.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Eski verileri temizleme
        productRepository.deleteAll();
        System.out.println("🔄 [VERİTABANI] Eski veriler temizlendi, güncel market kataloğu yükleniyor...");

        // Projenin modeliyle %100 uyumlu liste
        List<Product> initialCatalog = Arrays.asList(
                createProduct("Sut 1L (Temel Gida)", 29.5),
                createProduct("Cikolatali Gofret (Atistirmalik)", 10.0),
                createProduct("Sari Muz (Meyve)", 65.0),
                createProduct("Taze Domates (Sebze)", 28.0),
                createProduct("Soguk Iclecek Iced Tea", 24.5),
                createProduct("Maden Suyu (Icecek)", 12.5),
                createProduct("Siyah Zeytin 500g", 85.0),
                createProduct("Beyaz Peynir 1kg", 189.9),
                createProduct("Makarna 500g", 15.0),
                createProduct("Aycekirdegi Kuruyemis", 34.0),
                createProduct("Bulasik Deterjani", 52.5),
                createProduct("Patates 1kg (Sebze)", 22.0),
                createProduct("Kuru Sogan 1kg (Sebze)", 17.5),
                createProduct("Portakal Suyu 1L", 42.0),
                createProduct("Yogurt 1.5kg", 68.0),
                createProduct("Tam Buğday Ekmek", 12.0),
                createProduct("Mavi Yüzey Temizleyici", 45.0),
                createProduct("Sivi Sabun 500ml", 38.5),
                createProduct("Misir Cipsi (Atistirmalik)", 26.0),
                createProduct("Kritik Salatalik (Sebze)", 18.5),
                createProduct("Soguk Cola (Icecek)", 32.0),
                createProduct("Kirmizi Elma (Meyve)", 35.5)
        );

        productRepository.saveAll(initialCatalog);
        System.out.println("✅ [VERİTABANI] 22 adet dinamik market ürünü başarıyla senkronize edildi!");
    }

    private Product createProduct(String name, double price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setConfidenceScore(0.0);
        product.setAddedAt(LocalDateTime.now());
        return product;
    }
}