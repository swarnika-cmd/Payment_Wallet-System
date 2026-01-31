# 💰 PocketPay - Advanced Digital Wallet System

PocketPay is a high-performance, secure, and feature-rich digital wallet application built with **Spring Boot 3**. It is designed to handle real-world financial transaction scenarios with a focus on **performance**, **security**, and **scalability**.

This project demonstrates advanced backend engineering concepts including **Caching**, **Rate Limiting**, **Asynchronous Messaging**, and **Dynamic Search**.

---

## 🚀 Key Features & Highlights

### 🌟 Core Banking Features
*   **User Management**: Secure Registration & Login with JWT Authentication.
*   **Wallet Operations**: Create wallet, Check Balance.
*   **Transactions**:
    *   **Add Money**: Simulate loading money via payment gateway.
    *   **Transfer**: Instant P2P money transfer between users (Atomic Transactions).
    *   **Withdraw**: Move money back to a bank account.

### ⚡ Advanced implementation (Bonus Features)
*   **📈 Rate Limiting (DoS Protection)**
    *   Implemented **Token Bucket Algorithm** using **Bucket4j**.
    *   Limits API usage to **100 requests/minute** per user/IP to prevent abuse.
    *   Returns `429 Too Many Requests` with retry headers.
*   **🚀 High-Performance Caching**
    *   Integrated **Caffeine Cache** (In-Memory).
    *   Caches **User Profiles** and **Wallet Balances** to reduce Database hits by ~80% for read-heavy operations.
    *   Implements **Cache Eviction** strategies to ensure data consistency during transactions.
*   **📧 Asynchronous System**
    *   Uses `@Async` and `JavaMailSender` for non-blocking notifications.
    *   Sends instant **Transactional Emails** (Credit/Debit alerts) without slowing down the API response time.
*   **🔍 Advanced Search & Filtering**
    *   Implemented **JPA Specifications** (Criteria API) for dynamic querying.
    *   Users can filter transactions by **Date Range**, **Amount**, **Type**, and more simultaneously.
*   **📁 KYC Document Management**
    *   Secure **File Upload** system for KYC (Aadhaar/PAN).
    *   Validates file types (Images/PDF) and enforces size limits.
*   **📊 Analytics Dashboard APIs**
    *   SQL Aggregations (`SUM`, `COUNT`, `GROUP BY`) to provide insights.
    *   Endpoints for **Daily Transaction Volume** and **Transaction Type Distribution**.

---

## 🛠️ Technology Stack

*   **Framework**: Spring Boot 3.3 (Java 21)
*   **Database**: PostgreSQL 15 (Dockerized)
*   **Security**: Spring Security + JWT (JSON Web Tokens)
*   **Performance**: Caffeine Cache (L1 Cache), Bucket4j (Rate Limiting)
*   **Documentation**: OpenAPI / Swagger UI
*   **Build Tool**: Maven

---

## 🏗️ Architecture & Design Patterns
*   **Layered Architecture**: Controller -> Service -> Repository -> Database.
*   **DTO Pattern**: Separation of internal Entities and external API models.
*   **Repository Pattern**: For clean data access abstraction.
*   **Strategy Implementation**: Used for handling different transaction types logic.
*   **Observer/Event Pattern (Approximated)**: Using Async services for notifications.

---

## ⚙️ Setup & Installation

### Prerequisites
*   Java 17 or 21
*   Maven
*   PostgreSQL (or Docker)

### 1. Database Setup
The project is configured to use PostgreSQL.
```bash
# If using Docker
docker run --name pocketpay-db -e POSTGRES_DB=pocketpay -e POSTGRES_USER=pocketpay -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres
```

### 2. Configuration
Update `src/main/resources/application.properties` with your email credentials for notifications:
```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### 3. Run the Application
```bash
mvn spring-boot:run
```
The application will start on `http://localhost:9090`.

---

## 📚 API Documentation (Swagger)
Once the application is running, access the interactive API docs:
👉 **[http://localhost:9090/swagger-ui/index.html](http://localhost:9090/swagger-ui/index.html)**

---

## 🧪 Testing Methods

| Feature | Endpoint / Test Method |
| :--- | :--- |
| **Rate Limiting** | Spam `GET /api/documents` >100 times. Expect `429 Error`. |
| **Caching** | `GET /api/wallet/balance`. Response time drops drastically on 2nd hit |
| **Search** | `POST /api/wallet/transactions/search` with complex JSON criteria |
| **Async Email** | Perform a `Transfer`. Email arrives *after* API returns success |

---

## 🏆 Non-Functional Requirements Met
This project specifically targets high-value engineering habits:
1.  **Scalability**: Via Rate Limiting and Caching.
2.  **Reliability**: Atomic transactions and data consistency.
3.  **Observability**: Analytics endpoints.
4.  **Maintainability**: Clean code architecture and strict DTO usage.
