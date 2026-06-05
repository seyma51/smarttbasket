package com.example.smarttbasket.controller;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BasketControllerTest {

    @LocalServerPort
    private int port;

    // --- GARANTİLİ STATİK SAYAÇLAR ---
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;
    private static long getResponseTime = 0;
    private static long postResponseTime = 0;

    private static String sharedSelectedName = "";
    private static float sharedExpectedPrice = 0.0f;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    // --- TÜM KOŞUM BİTTİĞİNDE TERMİNALE RAPOR BASAN KISIM ---
    @RegisterExtension
    static AfterAllCallback reportSummary = new AfterAllCallback() {
        @Override
        public void afterAll(ExtensionContext context) {
            double successRate = totalTests > 0 ? ((double) passedTests / totalTests) * 100 : 0;

            System.out.println("\n==================================================");
            System.out.println("        🏆 AKILLI SEPET TEST RAPORU 🏆            ");
            System.out.println("==================================================");
            System.out.printf(" 📊 Toplam Koşulan Test : %d\n", totalTests);
            System.out.printf(" ✅ Başarılı Test Sayısı: %d\n", passedTests);
            System.out.printf(" ❌ Hatalı Test Sayısı  : %d\n", failedTests);
            System.out.printf(" 📈 TEST BAŞARI ORANI   : %.1f%%\n", successRate);
            System.out.println("--------------------------------------------------");
            System.out.printf(" ⏱️ [GET]  Ürün Listeleme Süresi : %d ms (Sınır: <2000ms)\n", getResponseTime);
            System.out.printf(" ⏱️ [POST] AI Resim Tarama Süresi: %d ms (Sınır: <3000ms)\n", postResponseTime);
            System.out.println("==================================================\n");
        }
    };

    @Test
    @Order(1)
    public void testScanAndAddProduct_ShouldReturnSavedProduct() throws java.io.IOException {
        totalTests++; // Sayaç manuel garantiye alındı

        Object[][] marketItems = {
                {"Sut 1L (Temel Gida)", 29.5f},
                {"Cikolatali Gofret (Atistirmalik)", 10.0f},
                {"Sari Muz (Meyve)", 65.0f},
                {"Taze Domates (Sebze)", 28.0f},
                {"Soguk Iclecek Iced Tea", 24.5f},
                {"Maden Suyu (Icecek)", 12.5f},
                {"Siyah Zeytin 500g", 85.0f},
                {"Beyaz Peynir 1kg", 189.9f},
                {"Makarna 500g", 15.0f},
                {"Aycekirdegi Kuruyemis", 34.0f},
                {"Bulasik Deterjani", 52.5f},
                {"Patates 1kg (Sebze)", 22.0f},
                {"Kuru Sogan 1kg (Sebze)", 17.5f},
                {"Portakal Suyu 1L", 42.0f},
                {"Yogurt 1.5kg", 68.0f},
                {"Tam Buğday Ekmek", 12.0f},
                {"Mavi Yüzey Temizleyici", 45.0f},
                {"Sivi Sabun 500ml", 38.5f},
                {"Misir Cipsi (Atistirmalik)", 26.0f},
                {"Kritik Salatalik (Sebze)", 18.5f},
                {"Soguk Cola (Icecek)", 32.0f},
                {"Kirmizi Elma (Meyve)", 35.5f}
        };

        int randomIndex = (int) (Math.random() * marketItems.length);
        sharedSelectedName = (String) marketItems[randomIndex][0];
        sharedExpectedPrice = (Float) marketItems[randomIndex][1];

        java.io.File mockImageFile = java.io.File.createTempFile("test_" + sharedSelectedName.replace(" ", "_"), ".jpg");
        java.nio.file.Files.writeString(mockImageFile.toPath(), "fake-image-content-for-testing");
        mockImageFile.deleteOnExit();

        System.out.println("\n🌐 >>> [TEST BAŞLADI] POST /api/basket/scan MARKET SİMÜLASYONU >>>");
        System.out.println("📸 Kameranın Taradığı Ürün: " + sharedSelectedName + " | Fiyat: " + sharedExpectedPrice + " TL");

        try {
            Response response = given()
                    .multiPart("image", mockImageFile)
                    .when()
                    .post("/api/basket/scan");

            postResponseTime = response.getTime();

            System.out.println("\n📥 <<< [CEVAP GELDİ] POST /api/basket/scan YANIT LOGLARI <<<");
            response.then()
                    .log().all()
                    .statusCode(200)
                    .contentType("application/json")
                    .body("id", notNullValue())
                    .body("name", equalTo(sharedSelectedName))
                    .body("price", is(sharedExpectedPrice));

            assertTrue(postResponseTime < 3000L, "Hata: POST isteği 3 saniyeden uzun sürdü!");
            passedTests++;
        } catch (Throwable t) {
            failedTests++;
            throw t;
        }
    }

    @Test
    @Order(2)
    public void testGetProductsInBasket_ShouldReturnSuccess() {
        totalTests++; // Sayaç manuel garantiye alındı
        System.out.println("\n🌐 >>> [TEST BAŞLADI] GET /api/basket/products İSTEK LOGLARI >>>");

        try {
            Response response = given()
                    .when()
                    .get("/api/basket/products");

            getResponseTime = response.getTime();

            System.out.println("\n📥 <<< [CEVAP GELDİ] GET /api/basket/products YANIT LOGLARI <<<");
            response.then()
                    .log().all()
                    .statusCode(200)
                    .contentType("application/json")
                    .body("$", instanceOf(java.util.List.class));

            assertTrue(getResponseTime < 2000L, "Hata: GET isteği 2 saniyeden uzun sürdü!");
            passedTests++;
        } catch (Throwable t) {
            failedTests++;
            throw t;
        }
    }
}