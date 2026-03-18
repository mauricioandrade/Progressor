<h1 align="center">
  <br/>
  Progressor
</h1>

<p align="center">
  <strong>Your Fitness Journey, Secured.</strong>
</p>

<p align="center">
  <img alt="Java" src="https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring_Boot-4.0.3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white"/>
  <img alt="React" src="https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black"/>
  <img alt="TypeScript" src="https://img.shields.io/badge/TypeScript-5-3178C6?style=for-the-badge&logo=typescript&logoColor=white"/>
  <img alt="PostgreSQL" src="https://img.shields.io/badge/PostgreSQL-16+-4169E1?style=for-the-badge&logo=postgresql&logoColor=white"/>
  <img alt="Tailwind CSS" src="https://img.shields.io/badge/Tailwind_CSS-4-06B6D4?style=for-the-badge&logo=tailwind-css&logoColor=white"/>
</p>

<p align="center">
  <img alt="License" src="https://img.shields.io/badge/License-MIT-22c55e?style=flat-square"/>
  <img alt="Status" src="https://img.shields.io/badge/Status-Active_Development-8b5cf6?style=flat-square"/>
  <img alt="Architecture" src="https://img.shields.io/badge/Architecture-Clean_Architecture-f97316?style=flat-square"/>
  <img alt="Security" src="https://img.shields.io/badge/Encryption-AES--256--GCM-ef4444?style=flat-square"/>
  <img alt="Tests" src="https://img.shields.io/badge/Tests-32%2B_Automated-16a34a?style=flat-square"/>
  <img alt="LGPD" src="https://img.shields.io/badge/Compliance-LGPD%20%7C%20GDPR-3b82f6?style=flat-square"/>
</p>

<p align="center">
  <a href="README.md"><img alt="English" src="https://img.shields.io/badge/🇺🇸-English-blue?style=for-the-badge"/></a>
  &nbsp;
  <a href="README-pt.md"><img alt="Português" src="https://img.shields.io/badge/🇧🇷-Português-green?style=for-the-badge"/></a>
</p>

---

> Progressor is a full-stack fitness management platform connecting students, personal trainers, and nutritionists. Built on **Clean Architecture** with end-to-end encryption of sensitive data, professional PDF reports, real-time food search powered by **Open Food Facts**, and a consent-based professional opt-in flow.

---

## 📋 Table of Contents

- [✨ Key Features](#-key-features)
- [🏗️ Architecture](#️-architecture)
- [🔐 Security](#-security)
- [🩺 V1.1 — Consent Flow & Workout Matrix](#-v11--consent-flow--workout-matrix)
- [🥗 Nutrition Module](#-nutrition-module)
- [📄 PDF Reports](#-pdf-reports)
- [🧪 Testing](#-testing)
- [🖥️ Tech Stack](#️-tech-stack)
- [⚙️ How to Run](#️-how-to-run)
- [🗺️ Roadmap](#️-roadmap)

---

## ✨ Key Features

### 👥 Multi-Role User System

| Role | Capabilities |
|---|---|
| 🎓 **Student** | View workouts & diet, log check-ins, upload progress photos, track water, run self-assessments |
| 💪 **Personal Trainer** | Manage students via consent flow, create workout plans with block hierarchy, leave feedback, generate PDF reports |
| 🥗 **Nutritionist** | Manage patients via consent flow, build meal plans with real Brazilian food data via Open Food Facts, generate diet PDF reports |

### 🏋️ Workout Management
- Structured workout plans with **WorkoutPlan → WorkoutBlock → WorkoutExercise** hierarchy
- **Spreadsheet Matrix View** — see the last 4 training sessions side-by-side per exercise, grouped by block
- Daily check-in system with gym frequency graph (`ContributionGraph`)
- Exercise history & personal records tracking
- Rest timer with floating pill UI
- Exportable PDF workout sheet via JasperReports

### 📊 Progress Tracking
- Body measurements timeline (weight, body fat %, 8 circumference points)
- **Visual Before & After Slider** — drag to compare progress photos over time
- Progress photo upload with student notes and professional feedback bubbles
- Role-aware feedback badges (🏋️ Personal Trainer / 🥗 Nutritionist)
- Edit and delete own feedback (author-only, enforced server-side)
- Exportable PDF progress report

### 🥗 Nutrition Module
- Real-time food search via the **Open Food Facts API** — a free, community-driven global database with no commercial paywall
- **Native Brazilian food data in PT-BR** — real supermarket brands and local products (e.g., "Frango Assado Bob's", "Pão de Queijo Forno de Minas") with names already in Portuguese
- Macro-nutrient breakdown per 100g and per meal (kcal, protein, carbs, fat)
- Meal plans grouped by BREAKFAST / LUNCH / DINNER / SNACK
- Daily water intake tracker with custom goals
- Cheat meal flag 🍔

### 🔑 Auth & Identity
- JWT authentication with `email`, `role`, and `userId` claims
- Stateless Spring Security filter chain
- Forgot password / reset password flow (token-based, 15-min expiry)
- User avatar upload (stored as encrypted `bytea`)

---

## 🏗️ Architecture

Progressor follows **Clean Architecture** with a strict dependency rule: outer layers depend on inner layers, never the reverse.

```
┌────────────────────────────────────────────────────────────────┐
│                         Infrastructure                         │
│  Controllers │ JPA Adapters │ Flyway │ JasperReports │ OFF   │
├────────────────────────────────────────────────────────────────┤
│                       Application Layer                        │
│            Use Cases │ DTOs │ Port Interfaces                  │
├────────────────────────────────────────────────────────────────┤
│                         Domain Layer                           │
│   Entities (User, Student, WorkoutPlan, MealPlan, Photo…)      │
└────────────────────────────────────────────────────────────────┘
```

**Key structural decisions:**

- **JPA JOINED inheritance** for `UserEntity` → `StudentEntity` / `PersonalTrainerEntity` / `NutritionistEntity`
- All Use Cases are **plain Java objects** — no Spring annotations — instantiated as `@Bean` in `UseCaseConfig`. This keeps domain logic framework-agnostic and trivially testable with Mockito.
- Repository interfaces live in `application.ports` (dependency inversion); JPA adapters implement them from the outside.
- **Flyway** manages all schema changes from V1.1 onwards (`V2__add_invites_and_workout_blocks.sql`), enabling reproducible, version-controlled migrations.

---

## 🔐 Security

### AES-256-GCM Column Encryption

Sensitive data is encrypted **at the persistence layer** using JPA `AttributeConverter`s before it ever reaches the database. No plaintext health data is ever written to disk.

```
Application ──► AttributeConverter ──► AES-256-GCM ──► PostgreSQL bytea / TEXT
                                            ▲
                                ENCRYPTION_KEY env var (32 bytes / 64 hex chars)
```

| Field | Converter | Reason |
|---|---|---|
| `progress_photos.photo_data` | `EncryptedByteArrayConverter` | Raw image bytes — most sensitive |
| `progress_photos.professional_feedback` | `EncryptedStringConverter` | Private coach communication |
| `progress_photos.student_notes` | `EncryptedStringConverter` | Patient-sensitive goals |
| `body_measurements.*` (sensitive columns) | `EncryptedStringConverter` | Health data (LGPD Art. 11) |
| `app_users.email` | ❌ Not encrypted | Must remain searchable for login |
| `app_users.password` | BCrypt (Spring Security) | Standard password hashing |

> **Why email is not encrypted:** AES-GCM uses a random nonce per encryption, producing a different ciphertext every call. This makes `WHERE email = ?` impossible. Email is not health data under LGPD Article 11, so this is both correct and intentional.

### LGPD / GDPR Compliance
- Health-related data (photos, measurements, feedback) encrypted at rest ✅
- Data accessible only by the subject or their explicitly designated professional ✅
- Users can delete their own photos and feedback ✅
- Passwords never stored in plaintext ✅

---

## 🩺 V1.1 — Consent Flow & Workout Matrix

Version 1.1 introduced two major architectural upgrades based on real-world personal trainer feedback.

### Professional Opt-In Consent Flow

Before V1.1, a trainer could directly assign themselves as a student's professional — no consent required. This was replaced with a proper **ConnectionRequest** flow:

```
Personal Trainer          Backend                      Student
     │                      │                             │
     │  POST /connections/invite                          │
     │─────────────────────►│                             │
     │                      │── saves PENDING request ───►│
     │                      │                             │
     │                      │◄── POST /connections/respond (accepted: true)
     │                      │                             │
     │                      │── ACCEPTED → assigns PT ───►│
```

- `ConnectionRequest` entity with `PENDING → ACCEPTED / REJECTED` state machine
- Students see pending invitations on their dashboard and accept or reject with a single tap
- Duplicate invite guard: a professional cannot send a second invite to the same student while one is still pending
- Supports both `COACH` and `NUTRI` roles through the same flow

### Workout Spreadsheet Matrix

A new toggle on the Workout view switches from the card-based layout to a compact **spreadsheet grid**:

- Rows = exercises, grouped under their **WorkoutBlock** (e.g., "Block A – Upper Body")
- Columns = **Semana 1 / 2 / 3 / 4** — the last 4 historical log entries per exercise
- Each cell shows the logged weight/reps and the date — the most recent session highlighted in emerald
- Fully responsive with a sticky exercise name column

### Database Migrations (Flyway)

V1.1 introduced Flyway to manage schema evolution safely:

```sql
-- V2__add_invites_and_workout_blocks.sql (excerpt)
CREATE TABLE connection_requests (
  id UUID PRIMARY KEY,
  professional_id UUID NOT NULL,
  student_id UUID NOT NULL REFERENCES students(user_id),
  professional_role VARCHAR(10) NOT NULL CHECK (professional_role IN ('COACH', 'NUTRI')),
  status VARCHAR(10) NOT NULL DEFAULT 'PENDING',
  CONSTRAINT uq_connection_request UNIQUE (professional_id, student_id, professional_role)
);

CREATE TABLE workout_plans ( … );
CREATE TABLE workout_blocks ( … );
ALTER TABLE workout_exercises ADD COLUMN block_id UUID REFERENCES workout_blocks(id) ON DELETE SET NULL;
```

---

## 🥗 Nutrition Module

### Open Food Facts Integration

```
User types "frango"
     │
     ▼
GET world.openfoodfacts.org/cgi/search.pl?search_terms=frango&lc=pt&cc=br
     │
     ▼
Open Food Facts: "Frango Grelhado  |  nutriscore: B  |  per 100g"
     │
     ▼
OpenFoodFactsClient maps nutriments → FoodItemResponse
     │
     ▼
Frontend: "Frango Grelhado  |  165kcal · P31g · C0g · G4g  (por 100g)"
```

**Why Open Food Facts:**
- **No API key or OAuth required** — stateless HTTP, zero vendor lock-in
- **Native PT-BR results** — the `lc=pt&cc=br` locale parameters return real Brazilian supermarket products with Portuguese names out of the box
- **Community-driven breadth** — branded products, regional items, and packaged goods that government databases simply don't have
- **Macros per 100g** — consistent, comparable nutritional baseline

**HTTP Client resilience (`OpenFoodFactsClient`):**
- JDK `java.net.http.HttpClient` — no external dependencies
- 30-second connect + request timeout
- `User-Agent: ProgressorApp/1.0` header
- Automatic redirect following
- Graceful empty-result handling

---

## 📄 PDF Reports

Powered by **JasperReports 6.21.3**:

| Report | Endpoint | Description |
|---|---|---|
| 🏋️ Workout Sheet | `GET /api/reports/workout-sheet/{studentId}` | Full exercise plan with sets / reps / weight |
| 📊 Progress Report | `GET /api/reports/progress/{studentId}` | Measurement history + evolution |
| 🥗 Meal Plan | `GET /api/reports/meal-plan/{studentId}` | Grouped meals with macro breakdown |

---

## 🧪 Testing

Progressor ships with **32+ automated tests** across both backend and frontend layers.

### Backend — JUnit 5 + Mockito

| Test Class | Scenarios |
|---|---|
| `SendConnectionRequestUseCaseTest` | Happy path saves PENDING, duplicate invite guard, professional not found, student not found |
| `RespondToConnectionRequestUseCaseTest` | COACH accept assigns trainer, NUTRI accept assigns nutritionist, reject updates status only, wrong student throws, already-accepted guard |
| `WorkoutHierarchyUseCaseTest` | Plan creation + validation, block creation linked to plan, domain invariants (negative position, blank name) |
| `ProgressorApplicationTests` | Spring context loads cleanly |

Run them:
```bash
cd backend
JAVA_HOME=/path/to/java25 ./mvnw test
```

### Frontend — Vitest + React Testing Library

| Test File | Scenarios |
|---|---|
| `WorkoutSpreadsheetView.test.tsx` | Empty state, plan+block headers, sets×reps+weight display, history logs in columns, null-safety on missing blocks/exercises, URL routing by studentId |
| `InviteNotification.test.tsx` | Empty list renders nothing, invite list displays, Accept → `POST` with `accepted: true`, Reject → `POST` with `accepted: false`, invite removed after response, error toast on failure |

Run them:
```bash
cd frontend
npm test
```

---

## 🖥️ Tech Stack

### Backend
| Technology | Version | Purpose |
|---|---|---|
| Java | **25** | Core language (records, switch expressions, pattern matching) |
| Spring Boot | **4.0.3** | Application framework |
| Spring Security | 4.0.x | JWT stateless auth, role-based authorization |
| Spring Data JPA / Hibernate | 4.0.x / 7.x | ORM, JOINED inheritance strategy |
| PostgreSQL | 16+ | Primary database |
| Flyway | 10.x | Version-controlled schema migrations |
| JasperReports | 6.21.3 | PDF generation from JRXML templates |
| JUnit 5 + Mockito | — | Backend unit testing |

### Frontend
| Technology | Version | Purpose |
|---|---|---|
| React | 19 | UI framework |
| TypeScript | 5 | Type safety |
| Vite | 8 | Build tool |
| Tailwind CSS | 4 | Utility-first styling with dark mode |
| react-router-dom | 7 | Client-side routing, protected routes |
| react-i18next | — | Bilingual UI (PT-BR / EN) |
| Vitest + React Testing Library | — | Component and unit testing |
| Axios | — | HTTP client |
| lucide-react | — | Icon library |
| Recharts | — | Macro ring chart, contribution graph |

### Infrastructure
| Technology | Purpose |
|---|---|
| Docker / Docker Compose | One-command local environment |
| Maven 3.9+ | Backend build & dependency management |

---

## ⚙️ How to Run

### Prerequisites
- Docker & Docker Compose
- Node.js 20+
- Java 25
- Maven 3.9+

### 1. Clone

```bash
git clone https://github.com/your-username/progressor.git
cd progressor
```

### 2. Configure environment variables

```bash
cp .env.example .env
```

Edit `.env`:

```env
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/progressorv1
SPRING_DATASOURCE_USERNAME=progressorv1
SPRING_DATASOURCE_PASSWORD=progressorv1

# Open Food Facts requires no API key — it is a free, open database

# AES-256 encryption key — generate with: openssl rand -hex 32
# WARNING: changing this after data exists will corrupt all encrypted rows
ENCRYPTION_KEY=replace_with_64_hex_chars
```

### 3a. Docker Compose (recommended)

```bash
docker-compose up --build
```

| Service | URL |
|---|---|
| Frontend | http://localhost:5173 |
| Backend API | http://localhost:8081/api |

### 3b. Manual (development)

```bash
# Backend
cd backend && ./mvnw spring-boot:run

# Frontend (separate terminal)
cd frontend && npm install && npm run dev
```

### 4. Register

Navigate to `http://localhost:5173/signup` and create an account as **Student**, **Personal Trainer** (requires CREF), or **Nutritionist** (requires CRN).

---

## 🗺️ Roadmap

### ✅ V1.0 — Completed
- [x] Multi-role auth (Student / Personal Trainer / Nutritionist)
- [x] Workout plan creation and PDF export
- [x] Nutrition module with Open Food Facts food search (native PT-BR results)
- [x] Progress photo upload with AES-256-GCM encryption
- [x] Before & After visual slider
- [x] Professional feedback bubbles (author-only edit/delete)
- [x] Body measurements tracking + PDF progress report
- [x] Water intake tracker, gym check-in frequency graph
- [x] Forgot / reset password flow
- [x] Dark mode + bilingual UI (PT-BR / EN)

### ✅ V1.1 — Completed
- [x] Professional opt-in consent flow (`ConnectionRequest` PENDING → ACCEPTED / REJECTED)
- [x] `WorkoutPlan → WorkoutBlock → WorkoutExercise` hierarchy
- [x] Workout Spreadsheet Matrix View (last 4 sessions per exercise)
- [x] Flyway database migrations (`V2__add_invites_and_workout_blocks.sql`)
- [x] 32+ automated tests (JUnit 5 backend + Vitest frontend)

### 🔄 In Progress
- [ ] Push notifications for student inactivity alerts (> 3 days without check-in)
- [ ] Student goals & personal notes module

### 🔮 Planned
- [ ] Mobile app (React Native)
- [ ] AI-powered workout suggestions based on student history
- [ ] Wearable integrations (Garmin, Apple Health, Google Fit)

---

<p align="center">
  Built with ❤️ by <strong>Mauricio Andrade</strong><br/>
  <sub>Portfolio project — Clean Architecture · Spring Boot 4 · AES-256 Encryption · React 19 · 32+ Automated Tests</sub>
</p>
