# 🍽️ Zomato Clone — Full-Stack Food Delivery Application

A full-stack food delivery web application inspired by Zomato, built with **React** on the frontend and **Spring Boot** on the backend. It supports four distinct user roles — Customer, Restaurant Owner, Delivery Partner, and Admin — with role-based dashboards, JWT authentication, and a complete order lifecycle from cart to delivery.

[![React](https://img.shields.io/badge/React-18.3-61DAFB?logo=react&logoColor=white)](https://react.dev)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Java%2021-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org)
[![Vercel](https://img.shields.io/badge/Frontend-Vercel-000000?logo=vercel&logoColor=white)](https://vercel.com)
[![Render](https://img.shields.io/badge/Backend-Render-46E3B7?logo=render&logoColor=white)](https://render.com)

---

## 📖 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Tech Stack](#-tech-stack)
- [System Architecture](#-system-architecture)
- [User Roles](#-user-roles)
- [Project Structure](#-project-structure)
- [Database Entities](#-database-entities)
- [Getting Started](#-getting-started)
- [Environment Variables](#-environment-variables)
- [API Documentation](#-api-documentation)
- [Deployment](#-deployment)
- [Testing](#-testing)
- [Challenges & Solutions](#-challenges--solutions)
- [Future Enhancements](#-future-enhancements)
- [Author](#-author)

---

## 🧾 Overview

This project is a real-world, production-style food delivery platform. Users can browse restaurants, manage carts, and place orders; restaurant owners can manage their menus and incoming orders; delivery partners can accept and track deliveries; and admins can oversee the entire platform.

The frontend and backend are decoupled and communicate over a REST API secured with JWT, with PostgreSQL for persistent storage in production and H2 for fast local development.

## ✨ Key Features

- 🔑 JWT-based login and authentication
- 🔒 Role-based authorization (Customer / Owner / Delivery / Admin)
- 🔍 Restaurant search and listing
- 🍽️ Menu and category management
- 🛒 Cart management
- 📦 Order placement and status tracking
- 🚴 Delivery assignment workflow
- 📊 Admin dashboard with platform-wide controls
- 🖼️ Image uploads via Cloudinary
- 📘 Interactive API documentation with Swagger / OpenAPI

## 🛠 Tech Stack

| Layer | Technologies |
|---|---|
| **Frontend** | React.js, Vite, Tailwind CSS, Axios, React Router DOM |
| **Backend** | Java 21, Spring Boot, Spring Security, Spring Data JPA, JWT (jjwt), Maven |
| **Database** | PostgreSQL (production), H2 (local development) |
| **File Storage** | Cloudinary |
| **API Docs** | springdoc-openapi (Swagger UI) |
| **Deployment** | Vercel (frontend), Render (backend + PostgreSQL), Docker |
| **Version Control** | GitHub |

## 🏗 System Architecture

```
User → React Frontend → Axios (REST calls) → Spring Boot Backend → PostgreSQL Database
```

- **Frontend** — handles UI rendering and client-side routing.
- **Backend** — handles business logic, validation, and orchestration across modules.
- **Database** — stores users, restaurants, menu items, carts, and orders.
- **JWT** — issued at login and validated on every protected endpoint by Spring Security.

## 👥 User Roles

| Role | Capabilities |
|---|---|
| **Customer** | Register/login, browse restaurants & menus, manage cart, place orders, view order history |
| **Restaurant Owner** | Create & manage restaurants, manage categories & menu items, accept/reject orders, update order status |
| **Delivery Partner** | View available deliveries, accept assignments, mark orders picked up / delivered, view delivery history |
| **Admin** | View dashboard statistics, manage users, approve/reject restaurants, manage platform-wide orders |

## 📁 Project Structure

```
zomato-clone/
├── zomato-clone-backend/
│   ├── src/main/java/com/novabyte/zomatoclone/
│   │   ├── admin/          # Admin dashboard & platform management
│   │   ├── user/           # Auth, users & roles
│   │   ├── restaurant/     # Restaurant management
│   │   ├── menu/           # Categories & menu items
│   │   ├── cart/           # Cart & cart items
│   │   ├── order/          # Orders & order status history
│   │   ├── delivery/       # Delivery assignments & partner profiles
│   │   ├── upload/         # Cloudinary image uploads
│   │   ├── security/       # JWT & Spring Security configuration
│   │   ├── config/         # App-wide configuration
│   │   └── common/         # Shared exceptions, enums, response wrappers
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── application-dev.yml
│   │   └── application-prod.yml
│   ├── Dockerfile
│   └── pom.xml
│
└── zomato-clone-frontend/
    ├── src/
    │   ├── api/             # Axios instance & API calls
    │   ├── auth/             # Auth context & guards
    │   ├── components/       # Shared UI components
    │   ├── layouts/          # Role-based layout shells
    │   ├── hooks/             # Custom React hooks
    │   ├── features/
    │   │   ├── customer/     # Restaurant listing, cart, checkout
    │   │   ├── owner/        # Restaurant & menu dashboard
    │   │   ├── delivery/     # Delivery dashboard
    │   │   └── admin/        # Admin dashboard
    │   ├── pages/            # Login, Register, Unauthorized, NotFound
    │   └── utils/
    ├── index.html
    ├── package.json
    ├── tailwind.config.js
    └── vite.config.js
```

## 🗄 Database Entities

**Main entities:** `User`, `UserRole`, `Restaurant`, `Category`, `MenuItem`, `Cart`, `CartItem`, `Order`, `OrderItem`, `OrderStatusHistory`, `DeliveryAssignment`, `DeliveryPartnerProfile`

**Relationships:**
- One user can have multiple roles
- One restaurant owner can own multiple restaurants
- One restaurant has many menu items
- One customer has one cart
- One order contains multiple order items

## 🚀 Getting Started

### Prerequisites

- Node.js 18+ and npm
- Java 21 and Maven
- PostgreSQL (for production-like local setup) — or rely on the bundled H2 database for quick local development

### Clone the repository

```bash
git clone https://github.com/Preetikumari44/zomato_clone_react_springboot.git
cd zomato_clone_react_springboot
```

### Backend setup

```bash
cd zomato-clone-backend
mvn spring-boot:run
```

Backend runs at **http://localhost:8080**

### Frontend setup

```bash
cd zomato-clone-frontend
npm install
npm run dev
```

Frontend runs at **http://localhost:5173**

## 🔐 Environment Variables

The backend reads the following variables (see `application.yml`):

| Variable | Description |
|---|---|
| `DB_URL` | PostgreSQL JDBC connection URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | Secret key used to sign JWT tokens |
| `JWT_EXPIRATION_MS` | Token expiry in milliseconds (default: `86400000` / 24h) |
| `CORS_ALLOWED_ORIGINS` | Allowed frontend origin(s) for CORS (default: `http://localhost:5173`) |
| `CLOUDINARY_CLOUD_NAME` | Cloudinary cloud name |
| `CLOUDINARY_API_KEY` | Cloudinary API key |
| `CLOUDINARY_API_SECRET` | Cloudinary API secret |

The frontend reads:

| Variable | Description |
|---|---|
| `VITE_API_BASE_URL` | Base URL of the backend API, e.g. `https://your-render-backend-url.onrender.com/api` |

## 📘 API Documentation

Interactive Swagger UI is available once the backend is running:

- **Local:** `http://localhost:8080/swagger-ui.html`
- **Production (Render):** `https://your-render-backend-url.onrender.com/swagger-ui.html`

Role-based API access:

| Role | Accessible APIs |
|---|---|
| Customer | Cart & order APIs |
| Restaurant Owner | Restaurant & menu APIs |
| Delivery Partner | Delivery APIs |
| Admin | Admin APIs |

## ☁️ Deployment

| | Backend | Frontend |
|---|---|---|
| **Platform** | Render | Vercel |
| **Runtime** | Docker | Vite |
| **Database** | Render PostgreSQL | — |
| **Config** | `render.yaml` | Root directory: `zomato-clone-frontend` |
| **Env var** | see table above | `VITE_API_BASE_URL` |

## ✅ Testing

- Frontend build verified with `npm run build`
- Backend package verified with Maven
- APIs tested through Swagger UI
- Restaurant listing endpoint verified
- Full local stack tested end to end (frontend + backend running together)

## 🧩 Challenges & Solutions

| Challenge | Solution |
|---|---|
| Configuring backend environment variables | Added safe defaults and profile-based configuration |
| PostgreSQL deployment on Render | Added a Render Blueprint via `render.yaml` |
| CORS between Vercel and Render | Updated CORS config to support Vercel domains |
| Java import errors | Fixed backend compilation issues |
| Local vs. production database profiles | Added H2 for local dev, PostgreSQL for production |
| Deployment-ready configuration | Added Dockerfile, `render.yaml`, and `vercel.json` |

## 🔮 Future Enhancements

- 💳 Online payment integration
- 📍 Real-time order tracking
- 🗺️ Google Maps integration
- 📧 Email notifications
- ⭐ Restaurant ratings and reviews
- 🔎 Advanced search and filters
- 📱 Mobile responsive improvements
- 📈 Admin analytics dashboard
- 🏷️ Coupon and discount system

## 👤 Author

**Preeti Kumari**
Repository: [github.com/Preetikumari44/zomato_clone_react_springboot](https://github.com/Preetikumari44/zomato_clone_react_springboot)

---

*Built to demonstrate practical, hands-on knowledge of React, Spring Boot, PostgreSQL, REST APIs, authentication, and cloud deployment workflows.*
