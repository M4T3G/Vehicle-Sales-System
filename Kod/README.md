# Birinci El Araç Satış Bilgi Sistemi

Bu proje, birinci el araç satış işlemlerini yönetmek için geliştirilmiş bir bilgi sistemidir.

## Özellikler

### Müşteriler için:
- Araç arama ve filtreleme
- Fiyat teklifi verme
- Deneme sürüşü talebi oluşturma
- Sipariş verme

### Adminler için:
- Araç ekleme ve düzenleme
- Stok yönetimi
- Satış raporları
- Satış tahminleri

## Teknik Gereksinimler

- Java 11 veya üzeri
- PostgreSQL 12 veya üzeri
- Maven

## Kurulum

1. PostgreSQL veritabanını kurun ve `schema.txt` dosyasındaki SQL komutlarını çalıştırın
2. `datas.txt` dosyasındaki örnek verileri veritabanına ekleyin
3. Projeyi Maven ile derleyin:
   ```bash
   mvn clean install
   ```
4. Uygulamayı çalıştırın:
   ```bash
   mvn exec:java -Dexec.mainClass="com.carsales.ui.LoginFrame"
   ```

## Veritabanı Yapılandırması

Veritabanı bağlantı bilgilerini `src/main/java/com/carsales/database/DatabaseConnection.java` dosyasında düzenleyin:

```java
private static final String URL = "jdbc:postgresql://localhost:5432/car_sales";
private static final String USER = "postgres";
private static final String PASSWORD = "postgres";
```

## Kullanım

1. Uygulamayı başlatın
2. Müşteri veya admin olarak giriş yapın
3. İlgili işlemleri gerçekleştirin
