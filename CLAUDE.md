# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Eldorado tracks Polish IT job-market trends by polling the JustJoin.it API (`https://api.justjoin.it/v2/user-panel/offers`), aggregating offer counts across the cartesian product of `City × Technology × ExperienceLevel`, and visualizing the time series in a React dashboard. A Spring Boot backend stores daily snapshots in MySQL; a React frontend renders ApexCharts time series.

## Commands

### Backend (Spring Boot 3.4 / Java 21)
Run from `backend/`:
- Run dev: `./mvnw spring-boot:run` (uses `application-dev.properties`, expects MySQL `eldorado` DB on `localhost:3306` with `root`/`root`)
- Build: `./mvnw package` (jar at `target/eldorado-0.0.1-SNAPSHOT.jar`)
- Tests: `./mvnw test`
- Single test: `./mvnw test -Dtest=HelloServiceTest` or `-Dtest=ClassName#methodName`
- Switch profile: `-Dspring-boot.run.profiles=prod` (or `SPRING_PROFILES_ACTIVE=prod`)

### Frontend (React 18 / CRA)
Run from `frontend/`:
- Dev server: `npm start` (port 3000, expects `REACT_APP_API_URL` to point at the backend's `/api`)
- Build: `npm run build`
- Tests: `npm test`

### Full stack via Docker
- Dev compose (MySQL + backend + frontend): `docker-compose up --build`
  - Backend exposed on `:40269`, frontend on `:40270`, MySQL on `:3306`
  - Reads `.env` at repo root (DB credentials, `LOGGING_PATH`, `REACT_APP_API_URL`)
- Prod: `docker-compose -f docker-compose.prod.yml up --build` (uses `.env.prod`)

## Architecture

### Backend package layout (`pl.prim.eldorado.*`)
The codebase is organized by feature, **not** by technical layer. Each feature folder contains its own controller/service/repository:

- `fetchoffers/` — outbound data collection. `Scheduler` runs daily at 5 AM (`@Scheduled(cron = "0 0 5 * * *")`) and calls `JobOfferStatisticsService.collectStatisticsForAllCombinations()`, which uses **WebFlux `WebClient` reactively** to walk every `City × Technology × ExperienceLevel` combo with configurable delays/retries. Failures are persisted to a separate `FailedOperation` table rather than dropped.
- `getoffers/` — read API. `JobOffersController` exposes `GET /api/levels/city/{city}/technology/{technology}` returning a list of `CityTechnologyOfferDto` sorted by date desc, then city, then technology.
- `trigger/` — audit/observability. Every fetch (scheduled or manual) writes a `FetchJobTrigger` row tagged `SCHEDULED` or `MANUAL`. `GET /api/trigger/newest` returns the last trigger time (the frontend uses this for the "last updated" label).
- `admin/` — `POST /api/admin/fetch-jobs` triggers a manual fetch. Auth is a single shared secret in the `X-Admin-Key` header, configured via `app.admin.secret-key`.
- `model/` — JPA entities (`stats/JobOfferStatistics`, `fails/FailedOperation`) and the **enums that drive the entire fetch matrix**: `City`, `Technology`, `ExperienceLevel`. Each enum's `displayName` is the value sent to the upstream JustJoin.it API, and the `ALL` variant means "no filter" — `urlBuilder` in `JobOfferStatisticsService` skips query params for `ALL`. Adding a new city/tech/level here automatically expands the daily fetch matrix.
- `config/` — `ApplicationConfig` builds the shared `WebClient` from `app.base-url`, `CorsConfig` reads `app.cors.allowed-origins` (comma-separated), `RequestLoggingInterceptor` logs incoming HTTP.
- `helloworldstuff/` — legacy/sample endpoints, not load-bearing.

### Persistence
- MySQL via Spring Data JPA. `spring.jpa.hibernate.ddl-auto=update` in both dev and prod — schema migrates implicitly from entity changes; there is no Flyway/Liquibase. Be careful with breaking entity changes against a populated DB.
- `JobOfferStatistics.offerCounts` is a `Map<ExperienceLevel, Integer>` mapped via `@ElementCollection` to a side table `offer_counts_mapping`.

### Reactive fetch pipeline
The fetch pipeline in `JobOfferStatisticsService` is fully reactive (`Flux`/`Mono`) with three nested levels (cities → technologies → experience levels), tunable via:
- `app.statistics.delay.requests` (seconds between experience-level requests)
- `app.statistics.delay.combinations` (seconds between city/tech combos)
- `app.statistics.concurrent.requests` (concurrency cap per `flatMap`)

These are deliberately throttled to avoid hammering the upstream API. Save uses `Schedulers.boundedElastic()` because JPA is blocking. Errors at the save level are captured to `FailedOperation` with retry/backoff before propagation.

### Frontend (`frontend/src/`)
Flat structure, five files. CRA + Material-UI + ApexCharts + axios.
- `App.js` — single-page app, fetches `/api/levels/...` and `/api/trigger/newest`, owns filter state (city, technology) and renders charts.
- `CustomChart.js` — wraps `react-apexcharts`.
- `chartOptions.js` — ApexCharts config (time-series, hover formatting).
- `config.js` — exports `apiUrl` from `REACT_APP_API_URL` env var (falls back to `http://localhost:8080/api`).

### CORS / environment wiring
The frontend's `REACT_APP_API_URL` must match an origin whitelisted by the backend's `app.cors.allowed-origins`. Defaults: dev allows `http://localhost:3000`; prod allows `https://eldorado.byst.re`.

## Key conventions

- Feature-folder package layout — when adding functionality, create a new package under `pl.prim.eldorado.<feature>/` rather than putting controllers/services into shared layer packages.
- `City.ALL` / `Technology.ALL` / `ExperienceLevel.ALL` are sentinels meaning "no filter" — `urlBuilder` and any consumer must handle them explicitly.
- DB schema evolves via Hibernate `ddl-auto=update`; treat entity edits as migrations.
- User-facing error strings in the frontend are in Polish (e.g., "Coś nie bangla") per the existing `.cursorrules`.
- Lombok is used heavily (`@Slf4j`, `@RequiredArgsConstructor`, `@Builder`, `@Getter`) — keep it consistent.
