🎬 Movie Ticket Booking System – Backend (Spring Boot)

This is the backend service for the Movie Ticket Booking System, powered by Spring Boot, MySQL, JWT Authentication, and Railway deployment. It provides all APIs required for browsing movies, managing shows, booking seats, and sending confirmation emails.

🚀 Live Backend URL

Base API URL: https://movie-backend-production-799d.up.railway.app/api

Example Endpoint:Movies: https://movie-backend-production-799d.up.railway.app/api/movies

🏗️ Tech Stack

Backend: Spring Boot 3, Spring Web, Spring Data JPA

Authentication: Spring Security + JWT

Database: MySQL

Email Service: Spring Mail

Deployment: Railway

Build Tool: Maven

📌 Core Features

🔐 User Module

User registration & login

JWT-based authentication

Booking history

🎞️ Movie Module

Add, update, delete movies

Fetch movie list

Movie posters, duration, genre, rating

🎭 Theater Module

Theater CRUD

Seat layout configuration

⏰ Show Module

Shows mapped to movies & theaters

Show timings management

💺 Seat Booking Module

Lock & reserve seats

Prevent double booking using seat-level locking

🎟️ Booking Module

Ticket booking flow

Price calculations

Booking & cancellation handling

💳 Payment Module

Razorpay/Stripe integration (or mock payment)

📩 Email Notification Module

Booking confirmation emails

Cancellation emails

📁 Project Structure

src
├── main
│   ├── java/com/app
│   │   ├── controller
│   │   ├── service
│   │   ├── repository
│   │   ├── model
│   │   └── security (JWT implementation)
│   └── resources
│       ├── application.properties
│       └── templates (optional)
└── test/java/com/app (JUnit tests)

⚙️ Environment Variables (Railway)

Set these variables in Railway → Variables:

DATABASE_URL=jdbc:mysql://...
DB_USER=your-username
DB_PASS=your-password
JWT_SECRET=your-secret-key
MAIL_USERNAME=your-email
MAIL_PASSWORD=your-app-password

▶️ Run Locally

1️⃣ Clone the Repository

git clone https://github.com/aravindhanl-commits/movie-backend.git
cd movie-backend

2️⃣ Configure Database

Update application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/moviedb
spring.datasource.username=root
spring.datasource.password=yourpass

3️⃣ Run the App

mvn spring-boot:run

Backend runs on:👉 http://localhost:8080

🧪 Testing

Unit Tests (JUnit + Mockito)

Service layer testing

Controller tests

Repository tests

Run tests:

mvn test

🚀 Deployment (Railway)

Create a new Railway project

Connect your GitHub repo

Add environment variables

Railway auto-builds & deploys

📌 API Examples

✔ Get All Movies

GET /api/movies

✔ Add Movie (Admin)

POST /api/movies
Authorization: Bearer <token>

✔ Book Seats

POST /api/bookings

📝 Submission Requirements

Full project pushed to GitHub

Backend deployed on Railway

Include environment variables



