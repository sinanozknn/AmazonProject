# Gauge + Java Web UI Test Projesi

Bu proje, Amazon.com.tr web sitesi için Gauge framework'ü ve Java kullanılarak geliştirilmiş bir UI test otomasyon projesidir.

## 📋 Gereksinimler

### Yerel Çalıştırma İçin
- **Java 17** veya üzeri
- **Maven 3.6+**
- **Gauge** framework
- **Chrome/Chromium** tarayıcı

### Docker ile Çalıştırma İçin
- **Docker** (20.10+)

## 🚀 Kurulum ve Çalıştırma

### 1. Yerel Ortamda Çalıştırma

#### Gereksinimler Kurulumu:
```bash
# Java 17 kurulumu (örnek: Ubuntu/Debian)
sudo apt update
sudo apt install openjdk-17-jdk

# Gauge kurulumu
curl -SsL https://downloads.gauge.org/stable | sh

# Gauge plugin'lerini yükle
gauge install java
gauge install html-report
gauge install xml-report
gauge install screenshot
```

#### Projeyi Çalıştırma:
```bash
# Bağımlılıkları yükle
mvn clean compile test-compile

# Tüm testleri çalıştır
gauge run specs

# Belirli bir spec dosyasını çalıştır
gauge run specs/AmazonCase.spec

# Paralel çalıştırma (4 thread)
gauge run --parallel -n 4 specs
```

### 2. Docker ile Çalıştırma

#### Docker Image'ını Oluştur:
```bash
# Proje dizininde
docker build -t amazon-test-project .
```

#### Testleri Çalıştır:
```bash
# Tüm testleri çalıştır
docker run --rm amazon-test-project

# Belirli spec dosyasını çalıştır
docker run --rm amazon-test-project gauge run specs/AmazonCase.spec

# Test raporlarını host sisteme kopyala
docker run --rm -v $(pwd)/reports:/app/reports amazon-test-project

# Debug modunda çalıştır
docker run --rm -it amazon-test-project bash
```

## 📁 Proje Yapısı

```
├── src/
│   ├── main/java/           # Ana kod dosyaları
│   │   ├── helper/          # Yardımcı sınıflar
│   │   └── model/           # Model sınıfları
│   └── test/java/           # Test kodları
│       ├── Base/            # Base test sınıfları
│       ├── Pages/           # Page Object sınıfları
│       └── step/            # Step implementasyonları
├── specs/                   # Gauge specification dosyaları
├── concept/                 # Gauge concept dosyaları
├── env/                     # Environment konfigürasyonları
├── reports/                 # Test raporları (otomatik oluşur)
├── webDriver/               # WebDriver executable'ları
├── pom.xml                  # Maven konfigürasyonu
├── manifest.json            # Gauge manifest
├── Dockerfile               # Docker konfigürasyonu
└── README.md               # Bu dosya
```

## 🧪 Test Senaryoları

Proje şu ana test senaryosunu içerir:

**Amazon Case:**
- Amazon.com.tr'ye git
- Kullanıcı girişi yap
- "laptop" anahtar kelimesiyle arama yap
- Fiyat aralığı filtresi uygula (32000-88000 TRY)
- Sonuçları fiyata göre sırala
- 7. ürünü aç ve yorumlarını kaydet
- Ürünü sepete ekle
- En çok yorumlu ürünler arasından en ucuz olanını sepete ekle

## 📊 Raporlar

Testler çalıştırıldıktan sonra `reports/html-report/` dizininde HTML formatında raporlar oluşur.

## 🛠️ Konfigürasyon

- **Browser ayarları:** `env/default/default.properties`
- **Java ayarları:** `env/default/java.properties`
- **Element locator'ları:** `src/main/resources/elementValues/`
- **Log konfigürasyonu:** `src/main/resources/log4j2.xml`

## 🐛 Sorun Giderme

### Docker ile ilgili sorunlar:
- Container içinde Chrome headless modda çalışır
- Display sorunları yaşarsanız `--headless` modunu kontrol edin
- Port çakışmaları için farklı portlar kullanın

### Yerel çalıştırma sorunları:
- Java versiyonunu kontrol edin: `java -version`
- Gauge kurulumunu kontrol edin: `gauge version`
- WebDriver yollarını kontrol edin

## 📝 Notlar

- Bu proje sadece test amaçlıdır
- CI/CD pipeline'ı dahil değildir
- Production ortamında kullanmadan önce güvenlik ayarlarını gözden geçirin
- Test verilerini gerçek verilerle değiştirmeyi unutmayın

---

**Geliştirici Notu:** Proje Docker ile kullanıma hazırdır ve herhangi bir ek kurulum gerektirmez.
