# URL Shortener Development Instructions

This is a full-stack URL shortener application with Java Spring Boot backend and React frontend.

## Project Overview

- **Backend**: Java Spring Boot REST API with H2 database
- **Frontend**: Modern React application with responsive design
- **Database**: H2 in-memory database for development
- **API**: RESTful endpoints for URL shortening and redirection

## Key Features Implemented

1. **URL Shortening**: Convert long URLs to short 6-character codes
2. **URL Redirection**: Redirect short codes to original URLs
3. **Click Tracking**: Count and track URL usage
4. **Validation**: Proper URL format validation
5. **Modern UI**: Beautiful, responsive React interface
6. **Error Handling**: Comprehensive error handling on both ends

## How to Run

### Method 1: Using VS Code Tasks
1. Open Command Palette (Cmd+Shift+P)
2. Type "Tasks: Run Task"
3. Select "Start Backend" to start the Spring Boot server
4. In a new terminal, select "Start Frontend" to start React dev server

### Method 2: Manual Commands
```bash
# Terminal 1 - Backend
cd backend
mvn clean install
mvn spring-boot:run

# Terminal 2 - Frontend  
cd frontend
npm install
npm start
```

## Testing the Application

1. Backend will run on `http://localhost:8080`
2. Frontend will run on `http://localhost:3000`
3. Visit `http://localhost:3000` to use the application
4. Visit `http://localhost:8080/h2-console` to view the database

## API Testing

You can test the API directly:

```bash
# Shorten a URL
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"originalUrl": "https://www.example.com"}'

# Visit the short URL
curl -L http://localhost:8080/{shortCode}

# Get stats
curl http://localhost:8080/api/stats/{shortCode}
```

## Project Structure

The application follows best practices:
- Clean architecture with separate layers (Controller, Service, Repository)
- DTOs for API communication
- JPA entities for database mapping
- React components with proper separation of concerns
- CSS modules for styling
- Error handling and validation

## Development Notes

- The backend uses H2 in-memory database that resets on restart
- CORS is configured to allow frontend at localhost:3000
- The React app uses a proxy to the backend API
- All URLs are validated on both frontend and backend
- Click tracking is implemented with automatic increment
