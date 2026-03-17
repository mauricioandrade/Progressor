<h1 align="center">
  <img src="frontend/src/assets/logoEscuro.png" alt="Progressor Logo" width="200"/>
  <br/>
  Progressor
</h1>

<p align="center">
  <strong>Your Fitness Journey, Secured.</strong><br/>
  <em>Sua Jornada Fitness, com Segurança.</em>
</p>

<p align="center">
  <img alt="Java" src="https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring_Boot-4.0.3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white"/>
  <img alt="React" src="https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black"/>
  <img alt="TypeScript" src="https://img.shields.io/badge/TypeScript-5-3178C6?style=for-the-badge&logo=typescript&logoColor=white"/>
  <img alt="PostgreSQL" src="https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white"/>
  <img alt="Tailwind CSS" src="https://img.shields.io/badge/Tailwind_CSS-3-06B6D4?style=for-the-badge&logo=tailwind-css&logoColor=white"/>
</p>

<p align="center">
  <img alt="License" src="https://img.shields.io/badge/License-MIT-green?style=flat-square"/>
  <img alt="Status" src="https://img.shields.io/badge/Status-Active_Development-blueviolet?style=flat-square"/>
  <img alt="Architecture" src="https://img.shields.io/badge/Architecture-Clean_Architecture-orange?style=flat-square"/>
  <img alt="Security" src="https://img.shields.io/badge/Encryption-AES--256--GCM-red?style=flat-square"/>
  <img alt="LGPD" src="https://img.shields.io/badge/Compliance-LGPD%20%7C%20GDPR-blue?style=flat-square"/>
</p>

---

## 🇧🇷 Português | 🇺🇸 English

> **PT:** Progressor é uma plataforma completa de gestão fitness que conecta alunos, personal trainers e nutricionistas. Com criptografia de ponta a ponta nos dados sensíveis, relatórios PDF profissionais e busca de alimentos em tempo real, o projeto foi construído com Clean Architecture e os mais modernos padrões da indústria.

> **EN:** Progressor is a full-stack fitness management platform connecting students, personal trainers, and nutritionists. With end-to-end encryption of sensitive data, professional PDF reports, and real-time food search, the project is built on Clean Architecture and modern industry standards.

---

## 📋 Table of Contents / Índice

- [✨ Key Features](#-key-features--funcionalidades-principais)
- [🏗️ Architecture](#️-architecture--arquitetura)
- [🔐 Security](#-security--segurança)
- [🥗 Nutrition Module](#-nutrition-module--módulo-de-nutrição)
- [📄 PDF Reports](#-pdf-reports--relatórios-pdf)
- [🖥️ Tech Stack](#️-tech-stack--stack-tecnológica)
- [⚙️ How to Run](#️-how-to-run--como-executar)
- [🗺️ Roadmap](#️-roadmap)
- [📸 Screenshots](#-screenshots)

---

## ✨ Key Features / Funcionalidades Principais

### 👤 Multi-Role User System
| Role / Papel | Capabilities / Capacidades |
|---|---|
| 🎓 **Student / Aluno** | View workout & diet, log check-ins, upload progress photos, track water intake, self-assessments |
| 💪 **Personal Trainer** | Manage students, create workout plans, view student dashboards, leave photo feedback, generate PDF reports |
| 🥗 **Nutritionist / Nutricionista** | Manage patients, build meal plans with USDA food search, generate diet PDF reports |

### 🏋️ Workout Management
- Create structured workout plans with sets, reps, weight, and rest time
- Daily check-in system with gym frequency tracking (ContributionGraph)
- Exercise history & personal records tracking
- 📄 Exportable PDF workout sheet via JasperReports
- Rest timer with floating pill UI (`RestTimerPill`)

### 📊 Progress Tracking
- Body measurements timeline (weight, body fat %, 8 circumference points)
- **Visual Before & After Slider** (`BeforeAfterSlider`) — drag to compare photos over time
- Progress photo upload with student notes and professional feedback bubbles
- Role-aware feedback badges (🏋️ Personal / 🥗 Nutri)
- Edit and delete own feedback (author-only, enforced server-side)
- 📄 Exportable PDF progress report with measurement history

### 🥗 Nutrition Module
- Real-time food search powered by the **USDA FoodData Central API**
- **Portuguese ↔ English translation layer** — search in PT-BR, display results in Portuguese
  (`"frango"` → queries `"Chicken"` → displays `"Frango, Peito, grelhado"`)
- Macro-nutrient breakdown per meal (kcal, protein, carbs, fat)
- Meal plans grouped by BREAKFAST / LUNCH / DINNER / SNACK
- Daily water intake tracker with custom goals
- Cheat meal flag 🍔
- 📄 Exportable PDF meal plan report

### 🔑 Auth & Identity
- JWT authentication with `email`, `role`, and `userId` claims
- Stateless Spring Security filter chain
- Forgot password / reset password flow (token-based, 15-min expiry)
- User avatar upload (stored as encrypted `bytea`)

---

## 🏗️ Architecture / Arquitetura

Progressor follows **Clean Architecture** with a strict dependency rule: outer layers depend on inner layers, never the reverse.

```
┌─────────────────────────────────────────────────────┐
│                   Infrastructure                    │
│  Controllers │ JPA Adapters │ JasperReports │ USDA  │
├─────────────────────────────────────────────────────┤
│                  Application Layer                  │
│        Use Cases │ DTOs │ Port Interfaces           │
├─────────────────────────────────────────────────────┤
│                   Domain Layer                      │
│   Entities (Student, Workout, MealPlan, Photo…)     │
└─────────────────────────────────────────────────────┘
```

**Key structural decisions:**
- **JPA JOINED inheritance** for `UserEntity` → `StudentEntity` / `PersonalTrainerEntity` / `NutritionistEntity`
- All Use Cases are plain Java objects — no Spring annotations — instantiated as `@Bean` in `UseCaseConfig`
- Repository interfaces live in the `application.ports` package (dependency inversion); JPA adapters implement them from the outside

---

## 🔐 Security / Segurança

### AES-256-GCM Column Encryption

Sensitive data is encrypted **at the persistence layer** using JPA `AttributeConverter`s before it ever reaches the database.

```
Application ──► AttributeConverter ──► AES-256-GCM ──► PostgreSQL bytea / TEXT column
                                          ▲
                              ENCRYPTION_KEY env variable (32 bytes / 64 hex chars)
```

| Field / Campo | Converter Used | Reason |
|---|---|---|
| `progress_photos.photo_data` | `EncryptedByteArrayConverter` | Raw image bytes — most sensitive |
| `progress_photos.professional_feedback` | `EncryptedStringConverter` | Private coach communication |
| `progress_photos.student_notes` | `EncryptedStringConverter` | Patient-sensitive goals |
| `body_measurements.*` (sensitive columns) | `EncryptedStringConverter` | Health data (LGPD Art. 11) |
| `app_users.email` | ❌ **Not encrypted** | Must remain searchable for login (`findByEmail`) |
| `app_users.password` | BCrypt (Spring Security) | Standard password hashing |

> ⚠️ **Why email is NOT encrypted:** Encrypting with a random nonce (AES-GCM) produces a different ciphertext on every call. This makes `WHERE email = ?` impossible. The email is not considered sensitive health data under LGPD Article 11, so this is both secure and correct.

### LGPD / GDPR Compliance Notes
- Health-related data (photos, measurements, feedback) is encrypted at rest ✅
- Data is accessed only by the data subject (student) or their designated professional ✅
- Users can delete their own photos (`DELETE /api/progress-photos/{id}`) ✅
- Passwords are never stored in plaintext ✅

---

## 🥗 Nutrition Module / Módulo de Nutrição

### USDA FoodData Central Integration

```
User types "frango"
     │
     ▼
BrazilianFoodTranslation.toEnglishQuery()   →  "Chicken"
     │
     ▼
GET api.nal.usda.gov/fdc/v1/foods/search
  ?api_key=...&query=Chicken&dataType=Foundation&dataType=SR+Legacy
     │
     ▼
USDA returns: "Chicken, broilers or fryers, breast, meat only, cooked, roasted"
     │
     ▼
BrazilianFoodTranslation.toPortugueseName()  →  "Frango, Peito, grelhado"
     │
     ▼
Frontend displays: "Frango, Peito, grelhado  |  165kcal · P31g · C0g · G4g"
```

**Macro Nutrient IDs mapped from USDA:**
| Nutrient | USDA ID |
|---|---|
| Protein | `1003` |
| Fat | `1004` |
| Carbohydrates | `1005` |
| Energy (kcal) | `1008` |

**HTTP Client resilience (`UsdaClient`):**
- JDK `java.net.http.HttpClient` — no extra dependencies
- 30-second connect + request timeout (avoids I/O hangs)
- `User-Agent: ProgressorApp/1.0` header (required by some government APIs)
- Automatic redirect following (`NORMAL` policy)
- Masked key in logs: `tMOF****WKMO`
- Explicit HTTP 403 branch with diagnostic message

---

## 📄 PDF Reports / Relatórios PDF

Powered by **JasperReports 6.21.3**, three report types are available:

| Report | Endpoint | Description |
|---|---|---|
| 🏋️ Workout Sheet | `GET /api/reports/workout-sheet/{studentId}` | Full exercise plan with sets/reps/weight |
| 📊 Progress Report | `GET /api/reports/progress/{studentId}` | Measurement history + evolution charts |
| 🥗 Meal Plan | `GET /api/reports/meal-plan/{studentId}` | Grouped meals with macro breakdown |

Reports are compiled from `.jrxml` templates at startup and streamed as `application/pdf` responses.

> **Important:** JRXML element order must follow the JasperReports schema strictly:
> `columnFooter → pageFooter → lastPageFooter → summary → noData`
> Violating this order causes a `SAXParseException` at compile time.

---

## 🖥️ Tech Stack / Stack Tecnológica

### Backend
| Technology | Version | Purpose |
|---|---|---|
| Java | **25** | Core language (uses switch expressions, records, sealed classes) |
| Spring Boot | **4.0.3** | Application framework |
| Spring Security | 4.0.x | JWT stateless auth, role-based authorization |
| Spring Data JPA | 4.0.x | ORM + repository layer |
| Hibernate | 7.x | JPA provider, JOINED inheritance strategy |
| PostgreSQL | 16 | Primary database |
| JasperReports | 6.21.3 | PDF report generation from JRXML templates |
| Auth0 Java JWT | 4.4.0 | JWT creation and validation |
| Jackson | 2.x | JSON parsing (USDA API responses) |

### Frontend
| Technology | Version | Purpose |
|---|---|---|
| React | 19 | UI framework |
| TypeScript | 5 | Type safety |
| Vite | 6 | Build tool |
| Tailwind CSS | 3 | Utility-first styling with dark mode |
| react-router-dom | 6 | Client-side routing, protected routes |
| react-i18next | — | Bilingual UI (PT-BR + EN) |
| react-hot-toast | — | Non-intrusive toast notifications |
| Axios | — | HTTP client |
| lucide-react | — | Icon library |
| jwt-decode | — | Client-side JWT claim extraction |

### Infrastructure
| Technology | Purpose |
|---|---|
| Docker / Docker Compose | One-command local environment |
| Maven | Backend build & dependency management |

---

## ⚙️ How to Run / Como Executar

### Prerequisites / Pré-requisitos
- Docker & Docker Compose
- Node.js 20+
- Java 25 (for local backend development)
- Maven 3.9+

### 1. Clone the repository

```bash
git clone https://github.com/your-username/progressor.git
cd progressor
```

### 2. Configure environment variables

```bash
cp .env.example .env
```

Edit `.env` with your values:

```env
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/progressorv1
SPRING_DATASOURCE_USERNAME=progressorv1
SPRING_DATASOURCE_PASSWORD=progressorv1

# USDA FoodData Central API
# Get your free key at: https://fdc.nal.usda.gov/api-guide.html
USDA_API_KEY=your_usda_api_key_here

# AES-256 Encryption key — generate with: openssl rand -hex 32
# WARNING: changing this key after data exists will corrupt all encrypted rows
ENCRYPTION_KEY=replace_with_64_hex_chars
```

### 3a. Run with Docker Compose (recommended)

```bash
# Start PostgreSQL + Backend + Frontend
docker-compose up --build
```

| Service | URL |
|---|---|
| Frontend | http://localhost:5173 |
| Backend API | http://localhost:8081/api |
| API Docs (Swagger) | http://localhost:8081/swagger-ui.html |

### 3b. Run manually (development)

**Backend:**
```bash
cd backend
./mvnw spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
```

### 4. Create your first account

Navigate to `http://localhost:5173/signup` and register as:
- **Personal Trainer** (requires CREF number)
- **Nutritionist** (requires CRN number)
- **Student**

---

## 🗺️ Roadmap

### ✅ Completed
- [x] Multi-role authentication (Student / Personal Trainer / Nutritionist)
- [x] Workout plan creation and PDF export
- [x] Nutrition module with USDA food search
- [x] PT→EN query translation + EN→PT result back-translation (85 food entries)
- [x] Progress photo upload with AES-256-GCM encryption
- [x] Before & After visual slider
- [x] Professional feedback bubbles (with edit/delete, author-only enforcement)
- [x] Body measurements tracking + PDF progress report
- [x] Water intake tracker with daily goals
- [x] Gym check-in frequency graph
- [x] Forgot password / Reset password flow
- [x] Dark mode + bilingual UI (PT-BR / EN)

### 🔄 In Progress
- [ ] Push notifications for student inactivity alerts (> 3 days without check-in)
- [ ] Student goals & notes module

### 🔮 Planned
- [ ] **FatSecret API migration** — replace USDA with FatSecret for localized Brazilian brand data (including "Frango Assado do Bob's", "Pão de Queijo Forno de Minas", etc.) and native Portuguese food names
- [ ] Mobile app (React Native)
- [ ] AI-powered workout suggestion based on student history
- [ ] Integration with wearables (Garmin, Apple Health, Google Fit)
- [ ] Multi-language report templates (PT-BR / EN)

---

## 📸 Screenshots

> _Screenshots will be added as the UI stabilizes._

| Dashboard (Dark) | Workout Builder | Diet Planner |
|---|---|---|
| _coming soon_ | _coming soon_ | _coming soon_ |

| Progress Photos | PDF Report | Student Detail |
|---|---|---|
| _coming soon_ | _coming soon_ | _coming soon_ |

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<p align="center">
  Built with ❤️ by <strong>Mauricio Andrade</strong><br/>
  <sub>Portfolio project — demonstrating Clean Architecture, Spring Boot 4, AES-256 encryption, and modern React patterns</sub>
</p>
