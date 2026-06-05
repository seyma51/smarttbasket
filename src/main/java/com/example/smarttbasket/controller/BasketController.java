package com.example.smarttbasket.controller;

import com.example.smarttbasket.model.Product;
import com.example.smarttbasket.service.BasketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/basket")
public class BasketController {

    private final BasketService basketService;

    // Bağımlılığı constructor ile temiz bir şekilde enjekte ediyoruz
    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    // 1. Kameradan gelen görseli alıp ürünü tespit eden ve sepete ekleyen API
    @PostMapping("/scan")
    public ResponseEntity<Product> scanProduct(@RequestParam("image") MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Gelen MultipartFile'ı yapay zekanın işleyebileceği geçici bir File nesnesine dönüştürüyoruz
        File tempFile = File.createTempFile("upload_", multipartFile.getOriginalFilename());
        multipartFile.transferTo(tempFile);

        // Sepet servisini tetikleyip ürünü veritabanına kaydediyoruz
        Product savedProduct = basketService.scanAndAddProduct(tempFile);

        // Geçici dosyayı işimiz bittiğinde diskten temizliyoruz
        tempFile.deleteOnExit();

        return ResponseEntity.ok(savedProduct);
    }

    // 2. Sepetteki tüm ürünleri listeleyen API
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(basketService.getAllProductsInBasket());
    }

    // 3. Sepetten ID ile ürün çıkaran API
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> removeProduct(@PathVariable Long id) {
        basketService.removeProductFromBasket(id);
        return ResponseEntity.noContent().build();
    }
}