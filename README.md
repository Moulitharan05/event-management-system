# Eventra — Event Management System

Full-stack event management platform: Spring Boot + MySQL + JWT security on the
backend, React (Vite) on the frontend, with email notifications, search/filters,
admin management, and attendance tracking.

```
ems/
├── backend/    Spring Boot 3 API (Java 17, MySQL, JWT, JavaMailSender)
└── frontend/   React + Vite SPA
```

## Feature map

| Spec requirement | Where it lives |
|---|---|
| User registration & authentication | `AuthController`, JWT (`JwtUtil`, `JwtAuthFilter`), React `AuthContext` |
| Event browsing | `GET /api/events`, `Events.jsx` |
| Search & filters (date/location/category) | `GET /api/events/search`, `EventRepository.search` |
| Event registration + email confirmation | `RegistrationController`, `EmailService.sendRegistrationConfirmation` |
| Admin: create/update/delete events, manage speakers | `AdminController`, `AdminDashboard.jsx` |
| Attendance tracking | `Registration.attended`, admin "Attendance" panel |
| Email reminders | `EventReminderScheduler` (hourly job, ~24h before event) |
| JWT (React frontend) | Stateless sessions, `Authorization: Bearer <token>` |
| JUnit 5 + Mockito tests | `EventServiceTest`, `RegistrationServiceTest`, `EventControllerTest`, `EventRepositoryTest` |

---

## 1. Prerequisites

- **Java 17+** and **Maven 3.9+**
- **MySQL 8+** running locally (or a cloud instance)
- **Node.js 18+** and npm
- A **Gmail account with an App Password** (for sending real emails) — see step 4

## 2. Set up the database

```sql
-- Optional: the app auto-creates the schema via createDatabaseIfNotExist=true,
-- but you can create it manually if you'd rather control it:
CREATE DATABASE event_management;
```

## 3. Configure the backend

All config lives in `backend/src/main/resources/application.properties`, but every
value can be overridden with an environment variable — recommended over editing the file directly.

Set these before running:

```bash
export DB_URL="jdbc:mysql://localhost:3306/event_management?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC"
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password

export JWT_SECRET=$(openssl rand -base64 48)   # generate your own secret — don't ship the default
export JWT_EXPIRATION_MS=86400000              # 24 hours

export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-16-char-app-password

export ADMIN_EMAIL=admin@eventms.com
export ADMIN_PASSWORD=Admin@123               # change this after first login
export ADMIN_NAME="System Admin"
```

### 4. Get a Gmail App Password (for JavaMailSender)

Gmail blocks plain-password SMTP login. You need an **App Password**:
1. Turn on 2-Step Verification on the Google account: https://myaccount.google.com/security
2. Go to https://myaccount.google.com/apppasswords
3. Create an app password named "Eventra" — copy the 16-character code
4. Use that as `MAIL_PASSWORD` (not your normal Gmail password)

If you skip this, registration still works — the app just logs a failed-send
warning instead of crashing (see `EmailService`).

## 5. Run the backend

```bash
cd backend
mvn spring-boot:run
```

The API starts on `http://localhost:8080`. On first boot, `DataSeeder` creates
an admin account using the `ADMIN_EMAIL` / `ADMIN_PASSWORD` you set above —
log in with those credentials to reach `/admin` on the frontend.

### Run the backend tests

```bash
cd backend
mvn test
```

This runs the JUnit 5 + Mockito service/controller tests and the `@DataJpaTest`
repository tests against an in-memory H2 database (see `src/test/resources/application.properties`) — no MySQL needed for tests.

## 6. Run the frontend

```bash
cd frontend
cp .env.example .env       # edit VITE_API_URL if your backend isn't on localhost:8080
npm install
npm run dev
```

Open `http://localhost:5173`. Sign up as a normal user to browse/register for
events, or sign in with the admin account to manage events, speakers, and
attendance at `/admin`.

## 7. Build the frontend for deployment

```bash
cd frontend
npm run build      # outputs static files to frontend/dist
```

---

## API reference (quick overview)

| Method | Endpoint | Auth | Purpose |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Create a user account, returns JWT |
| POST | `/api/auth/login` | Public | Log in, returns JWT |
| GET  | `/api/events` | Public | Upcoming events |
| GET  | `/api/events/search?keyword=&category=&venue=&fromDate=&toDate=` | Public | Filtered search |
| GET  | `/api/events/{id}` | Public | Event detail |
| GET  | `/api/speakers` | Public | List speakers |
| POST | `/api/registrations/events/{eventId}` | User | Register for an event |
| DELETE | `/api/registrations/events/{eventId}` | User | Cancel a registration |
| GET  | `/api/registrations/me` | User | My registrations |
| POST/PUT/DELETE | `/api/admin/events/**` | Admin | Manage events |
| POST/PUT/DELETE | `/api/admin/speakers/**` | Admin | Manage speakers |
| GET  | `/api/admin/events/{id}/attendees` | Admin | View registrants |
| PATCH | `/api/admin/registrations/{id}/attendance?attended=true` | Admin | Mark attendance |

All authenticated requests need `Authorization: Bearer <token>` — the React app
handles this automatically once you're logged in (`src/api/axios.js`).

---

## 8. Deployment

**Backend — Render / Railway / AWS EC2** (per the assignment's Option 1):
1. Push this repo to GitHub with `/backend` and `/frontend` folders as-is.
2. On Render/Railway: create a new Web Service pointing at `/backend`,
   build command `mvn clean package -DskipTests`, start command
   `java -jar target/event-management-system-1.0.0.jar`.
3. Add the environment variables from step 3 above in the platform's dashboard
   (use a managed MySQL add-on, or point `DB_URL` at your own instance).

**Frontend — Vercel / Netlify** (Option 2):
1. Import `/frontend` as the project root.
2. Build command `npm run build`, publish directory `dist`.
3. Set `VITE_API_URL` to your deployed backend's URL, e.g.
   `https://your-backend.onrender.com/api`.

**Walkthrough video (Option 3, optional):** record a 1–2 min screen capture of
signing up, browsing/searching events, registering, and the admin dashboard,
then upload to Google Drive with public link access.

---

## Notes & known simplifications

- Passwords are hashed with BCrypt; JWTs are HS256-signed and last 24h by default.
- Email sending is `@Async` and fails soft (logs an error) so a misconfigured
  mail account won't block registration — check backend logs if confirmation
  emails aren't arriving.
- The reminder job runs hourly and emails everyone registered for events
  landing in the next 23–24 hour window; for finer control, adjust the cron
  expression in `EventReminderScheduler`.
- MySQL schema is managed via `spring.jpa.hibernate.ddl-auto=update` — fine for
  this project size; swap in Flyway/Liquibase if you take this further.
