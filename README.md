# Farmora — Farm-to-Business Procurement Platform

A full-stack prototype: **React** (frontend) + **Java Spring Boot / JPA** (backend) + **MySQL** (database), implementing the core Farmora workflow from the spec — farmers post supply, buyers post demand, an AI-assisted matching engine connects them (with multi-farmer order aggregation), and a **distance finder** shows buyers exactly how far each farmer is.

## 📍 Distance Finder (the feature you asked for)

Implemented server-side in `DistanceService.java` using the **Haversine formula** on farmer/buyer latitude-longitude pairs. It's wired in at three points:

1. **Buyer search** — `GET /api/produce/search?crop=&buyerLat=&buyerLng=&maxDistanceKm=` (`ProduceController` → `ProduceService.searchForBuyer`) returns every matching, active produce listing with a computed `distanceKm` and `estimatedDeliveryTime`, sorted nearest-first, optionally capped by a max radius. This powers the **"📍 Find Nearby Farmers"** page in the buyer app.
2. **Matching engine** — distance is one of the 5 weighted factors (`20%`) in `MatchingService`, and every match result returned to `GET /api/matches/requirement/{id}` also carries the raw `distanceKm` so it's visible next to the match score.
3. **Order / logistics** — when a combined order is created, `OrderService` computes per-farmer distance and sums it into `totalDistanceKm`, which `LogisticsService` uses to estimate transport cost and delivery time for the order tracking screen.

## Project layout

```
farmora/
  backend/     Spring Boot 3 + JPA + MySQL REST API
  frontend/    React 18 SPA (react-router, axios)
```

## Backend setup

Requirements: Java 17+, Maven, MySQL 8+ running locally.

1. Create a MySQL user/password or use root — update `backend/src/main/resources/application.properties`:
   ```
   spring.datasource.url=jdbc:mysql://localhost:3306/farmora?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=root
   ```
   The database `farmora` and all tables are auto-created on first run (`spring.jpa.hibernate.ddl-auto=update`).

2. Run it:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   API is served at `http://localhost:8080/api`.

## Frontend setup

Requirements: Node.js 18+.

```bash
cd frontend
npm install
npm start
```
App runs at `http://localhost:3000` and calls the backend at `http://localhost:8080/api` (override with `REACT_APP_API_BASE_URL` in a `.env` file if needed).

## Demo walkthrough (matches the SIH demo script in the spec)

1. **Farmer Login** → register two farmers (different lat/lng, e.g. Bengaluru-area coordinates a few km apart) → each posts Tomato produce (300 kg @ ₹26/kg and 200 kg @ ₹27/kg).
2. **Buyer Login** → register a restaurant buyer with a delivery lat/lng → try **"📍 Find Nearby Farmers"** to see the distance finder return both farmers sorted by distance.
3. **Post Requirement** → 500 kg Tomato, Grade A, max ₹28/kg → lands on **AI Matched Farmers**, showing match % and distance for each.
4. Select both farmers (300 kg + 200 kg = 500 kg) → **CREATE COMBINED ORDER** — this is the multi-farmer aggregation feature.
5. Order screen shows the cost breakdown, auto-generated logistics (distance-based cost estimate), a status timeline, and a mock **Pay ₹X** button.
6. Step through the tracking buttons (Farmers Confirmed → Produce Collected → In Transit → Delivered → Buyer Confirmation) — the last step auto-releases payment.
7. **Admin** (`/admin`) shows platform-wide stats (farmer/buyer counts, GMV, delivery success rate).

## Notes on scope (prototype-appropriate, per the spec)

- Login is a **mock OTP** flow (any OTP value logs a registered phone in) — no real SMS/Firebase auth wired up, matching "don't build complex auth for the prototype."
- Payment is a **simulated flow** (`PENDING → SECURED → RELEASED`), no real escrow/payment gateway.
- Logistics cost/time are **formula-based estimates** (distance × rate), not a real fleet/routing integration.
- Price Intelligence and Demand Heatmap (spec §18–19) aren't included in this build — flag if you'd like those added; they'd follow the same pattern as the other modules.
