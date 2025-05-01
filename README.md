# E-commerce Spring Boot Application

This is a containerized Spring Boot-based e-commerce system with authentication, product and order management, real-time updates, and caching.

---

## 🧰 Requirements

- Java 17+
- Docker + Docker Compose

---

## ▶️ How to Run the Application (Docker)

1. **Build the JAR**

```bash
./mvnw.cmd clean package -DskipTests
```

2. **Build Docker Image**

```bash
docker compose build
```

3. **Run the App**

```bash
docker compose up
```

4. Access it at:  
   `http://localhost:8080`

---

## 🧪 Sample Request: Register User

**POST** `/auth/register`

```json
{
  "username": "owner1",
  "password": "password",
  "role": "ROLE_OWNER"
}
```

---

## 🔐 Authentication

All endpoints (except `/auth/register` and `/h2-console/**`) require **Basic Auth**.

- Use a tool like Postman or `curl`
- Example:
```bash
curl -u owner1:password http://localhost:8080/products
```

---

## 📦 Available Endpoints

### 👤 Authentication
| Method | Endpoint           | Description         |
|--------|--------------------|---------------------|
| POST   | `/auth/register`   | Register new user   |

---

### 🛒 Products
| Method | Endpoint             | Description                          |
|--------|----------------------|--------------------------------------|
| POST   | `/products`          | Add new product (owner only)         |
| PUT    | `/products/{id}`     | Update a product                     |
| DELETE | `/products/{id}`     | Delete a product                     |
| GET    | `/products`          | List all products                    |
| GET    | `/products/stream`   | Subscribe to real-time updates (SSE)|

---

### 🛍️ Cart
| Method | Endpoint | Description            |
|--------|----------|------------------------|
| POST   | `/cart`  | Add product to cart    |

---

### 📦 Orders
| Method | Endpoint         | Description                |
|--------|------------------|----------------------------|
| POST   | `/orders`        | Place order (customer)     |
| GET    | `/orders/insights` | Get product sales insights |

---

## 🧠 Features

- ✅ Spring Security (Basic Auth)
- ✅ Product CRUD (secured by role)
- ✅ Server-Sent Events for live updates
- ✅ Caffeine-based product caching
- ✅ In-memory H2 database
- ✅ Dockerized via Docker Compose

---

## 🧪 Testing

- Unit tests for services
- Integration tests for controllers
- Run with:

```bash
./mvnw.cmd test
```

---

## 🧼 Stopping the App

```bash
docker compose down
```

---

Happy coding! ✨
