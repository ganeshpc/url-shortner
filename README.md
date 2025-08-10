# URL Shortener

A production-ready URL shortener application built with Java Spring Boot backend and React (Vite) frontend.

## ✨ Features

- 🔗 Shorten long URLs into easy-to-share short links
- 📊 Click tracking and analytics
- 🚀 Fast and reliable service
- 🔒 Secure URL handling with rate limiting
- 📱 Responsive design
- ⚡ Real-time URL validation
- 🐳 Docker & Docker Compose ready
- 🌐 Production-ready with PostgreSQL and Redis
- 📈 Health checks and monitoring
- 🔧 Development and production environments

## 🛠️ Tech Stack

### Backend
- Java 17
- Spring Boot 3.2.1
- Spring Data JPA
- Spring Security
- PostgreSQL (production) / H2 (development)
- Redis (caching and rate limiting)
- Flyway (database migrations)
- Maven

### Frontend
- React 18
- Vite (build tool)
- React Router DOM
- Axios for API calls
- Modern CSS with responsive design

### Infrastructure
- Docker & Docker Compose
- Nginx (production frontend serving)
- Health checks and monitoring

## 🚀 Quick Start

### Development Environment

1. **Clone the repository**
```bash
git clone <repository-url>
cd url-shortener
```

2. **Set up Java 17 (Required for local development)**
```bash
./setup-java.sh
```

3. **Option A: Docker Development (Recommended)**
```bash
./start-dev.sh
```

4. **Option B: Local Development**
```bash
# Backend (in one terminal)
cd backend
export JAVA_HOME=/home/codespace/java/17.0.15-ms
export PATH=$JAVA_HOME/bin:$PATH
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Frontend (in another terminal)
cd frontend
npm install
npm run dev
```

**Access the application:**
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console
- Health Check: http://localhost:8080/actuator/health

### Production Deployment

1. **Set up environment**
```bash
cp .env.example .env
# Edit .env with your production values
```

2. **Deploy**
```bash
./deploy.sh
```

**Access the application:**
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- Health Check: http://localhost:8080/actuator/health

## Project Structure

```
url-shortener/
├── backend/                 # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/urlshortener/
│   │   │   │       ├── UrlShortenerApplication.java
│   │   │   │       ├── controller/
│   │   │   │       ├── service/
│   │   │   │       ├── repository/
│   │   │   │       ├── model/
│   │   │   │       └── dto/
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
└── frontend/               # React frontend
    ├── public/
    ├── src/
    │   ├── components/
    │   ├── App.js
    │   └── index.js
    └── package.json
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Node.js 16 or higher
- Maven 3.6 or higher

### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Build and run the Spring Boot application:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

The frontend will start on `http://localhost:3000`

## API Endpoints

### POST /api/shorten
Shorten a long URL.

**Request Body:**
```json
{
  "originalUrl": "https://example.com/very/long/url"
}
```

**Response:**
```json
{
  "shortCode": "abc123",
  "shortUrl": "http://localhost:8080/abc123",
  "originalUrl": "https://example.com/very/long/url"
}
```

### GET /{shortCode}
Redirect to the original URL and increment click count.

### GET /api/stats/{shortCode}
Get statistics for a short URL.

**Response:**
```json
{
  "id": 1,
  "originalUrl": "https://example.com/very/long/url",
  "shortCode": "abc123",
  "createdAt": "2025-01-18T10:30:00",
  "clickCount": 5
}
```

## Development Features

- **H2 Console**: Access the database console at `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (leave empty)

## Deployment

### Backend
Build the JAR file:
```bash
cd backend
mvn clean package
java -jar target/url-shortener-backend-0.0.1-SNAPSHOT.jar
```

### Frontend
Build for production:
```bash
cd frontend
npm run build
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.
