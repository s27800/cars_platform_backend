# Cars Platform Backend

REST API dla platformy do przeglądania i porównywania samochodów. Zbudowane z wykorzystaniem Spring Boot 3 i Java 21.

## 🚀 Stack technologiczny

| Technologia | Wersja | Opis |
|-------------|--------|------|
| Java | 21 | Język programowania |
| Spring Boot | 3.2.5 | Framework aplikacji |
| Spring Security | 6.x | Bezpieczeństwo + JWT |
| Spring Data JPA | 3.x | Warstwa persystencji |
| PostgreSQL | 16 | Baza danych |
| MapStruct | 1.5.5 | Mapowanie DTO |
| Lombok | 1.18.34 | Redukcja boilerplate |
| SpringDoc OpenAPI | 2.6.0 | Dokumentacja API (Swagger) |
| Maven | 3.9+ | Zarządzanie zależnościami |

## 📁 Struktura projektu

```
src/main/java/com/carsplatform/backend/
├── CarsPlatformBackendApplication.java    # Główna klasa aplikacji
├── api/                                    # Kontrolery REST
│   ├── admin/                              # Panel administracyjny
│   │   ├── AdminFuelReportController.java
│   │   └── AdminReviewController.java
│   ├── authentication/                     # Autentykacja
│   │   └── AuthenticationController.java
│   ├── bodyType/                           # Typy nadwozia
│   ├── brands/                             # Marki samochodów
│   ├── carImages/                          # Zdjęcia samochodów
│   ├── cars/                               # Samochody
│   ├── chassis/                            # Podwozie
│   ├── dataProposal/                       # Propozycje zmian danych
│   ├── engines/                            # Silniki
│   ├── fuelReportLikes/                    # Polubienia raportów paliwa
│   ├── fuelReports/                        # Raporty zużycia paliwa
│   ├── generations/                        # Generacje modeli
│   ├── insideDimensions/                   # Wymiary wewnętrzne
│   ├── likes/                              # Polubienia recenzji
│   ├── models/                             # Modele samochodów
│   ├── outsideDimensions/                  # Wymiary zewnętrzne
│   ├── performances/                       # Osiągi
│   ├── reviews/                            # Recenzje
│   ├── tags/                               # Tagi
│   ├── transmissions/                      # Skrzynie biegów
│   ├── users/                              # Użytkownicy
│   └── userSettings/                       # Ustawienia użytkowników
└── common/                                 # Wspólne komponenty
    ├── json/                               # Konfiguracja JSON
    ├── resourceExceptions/                 # Obsługa wyjątków
    ├── security/                           # Konfiguracja bezpieczeństwa
    │   ├── JwtAuthenticationFilter.java
    │   ├── JwtService.java
    │   └── SecurityConfig.java
    └── standard/                           # Standardowe klasy bazowe
```

## 🔌 API Endpoints

### Autentykacja
| Metoda | Endpoint | Opis |
|--------|----------|------|
| POST | `/api/auth/register` | Rejestracja użytkownika |
| POST | `/api/auth/login` | Logowanie (zwraca JWT) |
| GET | `/api/auth/me` | Dane zalogowanego użytkownika |

### Samochody
| Metoda | Endpoint | Opis |
|--------|----------|------|
| GET | `/api/cars` | Lista samochodów (z filtrami) |
| GET | `/api/cars/{id}` | Szczegóły samochodu |
| GET | `/api/cars/search` | Wyszukiwanie samochodów |

### Marki i modele
| Metoda | Endpoint | Opis |
|--------|----------|------|
| GET | `/api/brands` | Lista marek |
| GET | `/api/brands/{id}` | Szczegóły marki |
| GET | `/api/models` | Lista modeli |
| GET | `/api/generations` | Lista generacji |

### Recenzje
| Metoda | Endpoint | Opis |
|--------|----------|------|
| GET | `/api/reviews` | Lista recenzji |
| POST | `/api/reviews` | Dodanie recenzji 🔒 |
| PUT | `/api/reviews/{id}` | Edycja recenzji 🔒 |
| DELETE | `/api/reviews/{id}` | Usunięcie recenzji 🔒 |

### Raporty zużycia paliwa
| Metoda | Endpoint | Opis |
|--------|----------|------|
| GET | `/api/fuel-reports` | Lista raportów |
| POST | `/api/fuel-reports` | Dodanie raportu 🔒 |
| POST | `/api/fuel-report-likes/{id}` | Polubienie raportu 🔒 |

### Polubienia recenzji
| Metoda | Endpoint | Opis |
|--------|----------|------|
| POST | `/api/likes/review/{reviewId}` | Polubienie/odlubienie recenzji 🔒 |
| GET | `/api/likes/review/{reviewId}/status` | Status polubienia recenzji 🔒 |

### Administracja
| Metoda | Endpoint | Opis |
|--------|----------|------|
| GET | `/api/admin/reviews` | Lista recenzji do moderacji 🔒👑 |
| PUT | `/api/admin/reviews/{id}/approve` | Zatwierdzenie recenzji 🔒👑 |
| DELETE | `/api/admin/reviews/{id}` | Odrzucenie recenzji 🔒👑 |
| GET | `/api/admin/fuel-reports` | Lista raportów do moderacji 🔒👑 |

> 🔒 - wymaga autentykacji | 👑 - wymaga roli ADMIN

## 🛠️ Instalacja

### Wymagania

- Java 21+
- Maven 3.9+
- PostgreSQL 16+

### Konfiguracja bazy danych

1. Utwórz bazę danych PostgreSQL:

```sql
CREATE DATABASE cars_platform_db;
```

2. Wykonaj skrypty inicjalizujące (z katalogu `docker/init/`):
   - `01-schema.sql` - schemat bazy danych
   - `02-test-data.sql` - dane testowe

### Uruchomienie lokalne

```bash
# Klonowanie repozytorium
git clone https://github.com/s27800/cars_platform_backend.git
cd cars_platform_backend

# Build projektu
mvn clean install

# Uruchomienie aplikacji
mvn spring-boot:run
```

Aplikacja będzie dostępna pod adresem: `http://localhost:8080`

## ⚙️ Konfiguracja

### application.properties

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/cars_platform_db
spring.datasource.username=postgres
spring.datasource.password=admin

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

# JWT
app.jwt.secret=YOUR_SECRET_KEY
app.jwt.expiration-in-ms=86400000

# Szyfrowanie danych osobowych w spoczynku
app.encryption.key=BASE64_32_BAJTY
app.encryption.index-key=BASE64_32_BAJTY_INNE
```

### Zmienne środowiskowe (produkcja)

| Zmienna | Opis |
|---------|------|
| `SPRING_DATASOURCE_URL` | URL bazy danych |
| `SPRING_DATASOURCE_USERNAME` | Użytkownik bazy |
| `SPRING_DATASOURCE_PASSWORD` | Hasło bazy |
| `APP_JWT_SECRET` | Klucz JWT (min. 256 bitów) |
| `APP_JWT_EXPIRATION_IN_MS` | Czas życia tokenu (ms) |
| `APP_ENCRYPTION_KEY` | Klucz szyfrowania danych osobowych (Base64, dokładnie 32 bajty) |
| `APP_ENCRYPTION_INDEX_KEY` | Klucz indeksu ślepego adresu e-mail (Base64, 32 bajty, inny niż powyższy) |

## 📚 Dokumentacja API

### Swagger UI

Po uruchomieniu aplikacji dokumentacja dostępna jest pod:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Przykładowe zapytania

```bash
# Logowanie
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "user", "password": "password"}'

# Pobranie samochodów (z filtrem marki)
curl "http://localhost:8080/api/cars?brandId=1&page=0&size=10"

# Dodanie recenzji (z tokenem JWT)
curl -X POST http://localhost:8080/api/reviews \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"carId": 1, "rating": 5, "content": "Świetny samochód!"}'
```

## 🔐 Bezpieczeństwo

### Autentykacja JWT

1. Użytkownik loguje się przez `/api/auth/login`
2. Serwer zwraca token JWT
3. Token należy dołączać do każdego żądania w nagłówku:
   ```
   Authorization: Bearer <token>
   ```

### Role użytkowników

| Rola | Uprawnienia |
|------|-------------|
| `USER` | Przeglądanie, dodawanie recenzji i raportów, polubienia |
| `ADMIN` | Wszystkie uprawnienia USER + moderacja treści |

### Szyfrowanie danych osobowych w spoczynku

Adres e-mail, imię i nazwisko są zapisywane w bazie wyłącznie jako kryptogram AES-256-GCM,
z losowym wektorem inicjującym dla każdej wartości. Konwertery JPA szyfrują je przy zapisie
i odszyfrowują przy odczycie, więc reszta kodu operuje na wartościach jawnych.

Ponieważ ten sam adres szyfruje się za każdym razem inaczej, unikalności nie da się wymusić
na kolumnie `email`. Służy do tego `email_hash` - deterministyczny HMAC-SHA256 adresu,
liczony na osobnym kluczu i utrzymywany w zgodzie z adresem przez `UserEmailHashListener`.

Hasło nie jest szyfrowane, tylko haszowane funkcją BCrypt - to operacja jednokierunkowa
i właściwe rozwiązanie dla danych uwierzytelniających.

Mechanizm chroni dane w razie ujawnienia zrzutu bazy, kopii zapasowej lub wolumenu. Nie
chroni przed przejęciem samej aplikacji serwerowej, która dysponuje kluczem. **Utrata
`APP_ENCRYPTION_KEY` oznacza bezpowrotną utratę tych danych** - kopia klucza musi być
przechowywana niezależnie od kopii bazy.

Klucze pochodzą wyłącznie ze zmiennych środowiskowych. Wyjątkiem jest zestaw
demonstracyjny w `.env.example`: konta przykładowe w `docker/init/02-test-data.sql` są
zaszyfrowane właśnie nim, żeby `docker compose up` dawał działający stos z czytelnymi
danymi. Każde wdrożenie inne niż to demo wymaga wygenerowania własnej pary kluczy -
wtedy konta przykładowe przestają być odczytywalne i bazę należy postawić od zera.

### Zabezpieczone zasoby

- Endpointy GET są publiczne (przeglądanie)
- Endpointy POST/PUT/DELETE wymagają autentykacji
- Endpointy `/api/admin/**` wymagają roli ADMIN

## 🧪 Testowanie

```bash
# Uruchomienie wszystkich testów
mvn test

# Testy z raportem pokrycia
mvn test jacoco:report

# Uruchomienie konkretnego testu
mvn test -Dtest=CarControllerTest
```

### Konfiguracja testowa

Testy używają bazy H2 in-memory (konfiguracja w `src/test/resources/application-test.properties`).

## 🐳 Docker

### Build obrazu

```bash
docker build -t cars-platform-backend .
```

### Uruchomienie kontenera

```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/cars_platform_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=admin \
  cars-platform-backend
```

## 📊 Healthcheck

Aplikacja udostępnia endpoint health check poprzez Spring Actuator:

```bash
curl http://localhost:8080/actuator/health
```

## 🔗 Powiązane

- [Frontend Repository](https://github.com/s27800/cars_platform_frontend) - React SPA
