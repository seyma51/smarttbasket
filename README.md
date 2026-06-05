Bu projede, reyon kameralarından gelen ürün fotoğraflarını arka planda yapay zeka ile işleyen ve sepet süreçlerini otomatikleştiren Spring Boot tabanlı bir backend mimarisi geliştirdim. Projenin asıl odak noktası, sistemin kararlılığını ve performans sınırlarını yazılım kalitesi standartlarına uygun olarak entegrasyon testleriyle doğrulamaktır.

Kullanılan Teknolojiler ve Altyapı

Sistemin taşınabilir ve esnek olması için veritabanı katmanında PostgreSQL kullandım ve yerel kurulum karmaşasından kaçınmak için bu veritabanını tamamen Docker konteyner altyapısı üzerinde ayağa kaldırdım. Backend tarafında Spring Boot REST API mimarisini tercih ederken, nesne tespiti süreçleri için Deep Java Library entegrasyonuyla birlikte YOLOv8 modelini kullandım. Test otomasyon omurgasını ise Java ve Maven ortamında JUnit 5 ve REST Assured kütüphaneleriyle uçtan uca kurguladım.

Uygulanan Test Stratejisi

Yazılım geliştirme süreçlerindeki bakım maliyetini düşürmek ve testlerin sebepsiz yere çökmesini engellemek için iki temel entegrasyon testi yazdım:

İlk olarak POST metoduyla çalışan ürün tarama endpoint'ini test ettim. Kameradan gelen anlık resim gönderimini simüle etmek için HTTP Multipart yapısını kullandım. Yazdığım dinamik test veri havuzu sayesinde, sistem süt, peynir, makarna ve cips gibi 22 farklı gerçek market ürününü kuruşu kuruşuna ve ismi ismi Hamcrest Matchers fonksiyonlarıyla doğrulayabiliyor. Ayrıca bu tarama sürecinin 3 saniyenin altında yanıt vermesini performans sınırı olarak zorunlu kıldım.

İkinci olarak GET metoduyla çalışan sepet listeleme endpoint'ini test ettim. Eklenen ürünlerin Docker üzerinde koşan veritabanından bir liste halinde HTTP 200 OK koduyla başarıyla döndüğünü denetledim. Bu listeleme işleminin de 2 saniyenin altında tamamlandığını otomatik olarak ölçüyorum.
