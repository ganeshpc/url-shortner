# URL Shortener Backend

A robust URL shortening service built with Spring Boot, providing RESTful APIs for creating and accessing shortened URLs.

## Features

- **URL Shortening**: Convert long URLs into short, memorable codes
- **Multiple Generation Strategies**: Hash-based, counter-based, and random generation
- **Customizable Character Sets**: Support for different encoding schemes
- **Comprehensive Validation**: Input validation with detailed error messages
- **CORS Support**: Configurable cross-origin resource sharing
- **Database Persistence**: H2/PostgreSQL support with JPA
- **RESTful API**: Clean, well-documented endpoints

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Data JPA**
- **H2 Database** (development)
- **PostgreSQL** (production)
- **Spring Validation**
- **JUnit 5** (testing)
- **JaCoCo** (code coverage)

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Running the Application

```bash
# Clone the repository
git clone <repository-url>
cd backend

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### API Endpoints

#### Create Short URL
```bash
POST /api/shorten
Content-Type: application/json

{
  "originalUrl": "https://example.com"
}
```

#### Access Short URL
```bash
GET /{shortCode}
```

#### Get URL Statistics
```bash
GET /api/stats/{shortCode}
```

## Configuration

### Application Properties

The application can be configured via `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop

# CORS Configuration
app.cors.allowed-origins=http://localhost:3000

# Application URLs
app.base-url=http://localhost:8080
app.frontend-url=http://localhost:3000

# Short Code Generation
app.shortcode.length=7
app.shortcode.max-retry-attempts=5
app.shortcode.strategy=MIXED
app.shortcode.character-set=BASE58
```

### Short Code Configuration

- **Length**: 4-12 characters (default: 7)
- **Strategy**: HASH_BASED, COUNTER_BASED, RANDOM, MIXED
- **Character Set**: BASE62, BASE58, ALPHANUMERIC

## Testing

### Test Structure

```
src/test/java/
├── com/urlshortener/
│   ├── UrlShortenerApplicationTests.java    # Application context tests
│   ├── config/                              # Configuration tests
│   │   ├── AppConfigTest.java
│   │   ├── AppPropertiesTest.java
│   │   ├── ShortCodePropertiesTest.java
│   │   └── ValidatedAppPropertiesTest.java
│   ├── controller/                          # Controller tests
│   │   └── UrlControllerTest.java
│   ├── dto/                                 # Data transfer object tests
│   │   ├── ShortenUrlRequestTest.java
│   │   └── ShortenUrlResponseTest.java
│   ├── model/                               # Entity tests
│   │   └── UrlTest.java
│   ├── repository/                          # Repository tests
│   │   └── UrlRepositoryTest.java
│   └── service/                             # Service tests
│       ├── ShortCodeGeneratorTest.java
│       └── UrlShortenerServiceTest.java
```

### Running Tests

#### Run All Tests
```bash
mvn test
```

#### Run Fast Unit Tests Only (Recommended)
```bash
# Run all unit tests, exclude slow integration tests
mvn test -Dtest="!*IntegrationTest"
```

#### Run Specific Test Classes
```bash
# Run only unit tests (exclude integration tests)
mvn test -Dtest="!*ApplicationTest*"

# Run configuration tests
mvn test -Dtest="*PropertiesTest,*ConfigTest"

# Run service tests
mvn test -Dtest="*ServiceTest"

# Run controller tests
mvn test -Dtest="*ControllerTest"

# Run API tests
mvn test -Dtest="*ApiTest"

# Run integration tests (slower, full context)
mvn test -Dtest="*IntegrationTest"
```

#### Run Single Test Class
```bash
mvn test -Dtest=UrlShortenerServiceTest
```

#### Run Single Test Method
```bash
mvn test -Dtest=UrlShortenerServiceTest#testShortenUrl_Success
```

### Code Coverage

#### Generate Coverage Report
```bash
# Run tests and generate coverage report
mvn test jacoco:report

# Or generate report from existing test data
mvn jacoco:report
```

#### View Coverage Report
The HTML coverage report will be generated at:
```
target/site/jacoco/index.html
```

#### Command Line Coverage Report
Use the provided script for a quick coverage summary:
```bash
./coverage-report.sh
```

### Test Coverage Goals

- **Unit Tests**: >80% coverage
- **Integration Tests**: Key workflows covered
- **API Tests**: All endpoints tested
- **Configuration**: 100% coverage

### Current Coverage Status

```
Overall Project Coverage:
Instructions: 24% (144/579 covered)
Branches: 14% (6/42 covered)
Lines: 25% (35/138 covered)
Methods: 27% (12/44 covered)
Classes: 45% (5/11 covered)
```

## API Testing

### Setup for API Tests

API tests use Spring's MockMvc for testing REST endpoints with mocked services for fast unit testing.

#### Test Structure
```
src/test/java/com/urlshortener/api/
├── UrlShortenerApiTest.java           # Controller layer tests with mocked services
├── SimpleApiTest.java                 # Basic API functionality tests
├── ApiTestConfig.java                 # Test-specific configuration
└── UrlShortenerApiIntegrationTest.java # Full integration tests (optional)
```

#### Key Testing Approaches

1. **Unit API Tests** (`@WebMvcTest`): Fast tests with mocked services - **Recommended for regular testing**
2. **Integration API Tests** (`@SpringBootTest`): Full application context tests - **Optional, slower**
3. **Contract Tests**: Verify API contracts and responses

#### Example API Test
```java
@WebMvcTest
class UrlShortenerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlShortenerService urlShortenerService;

    @Test
    void testCreateShortUrl() throws Exception {
        // Mock the service
        Url mockUrl = new Url("https://example.com", "abc123");
        when(urlShortenerService.shortenUrl(anyString())).thenReturn(mockUrl);

        // Test the API
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"originalUrl\":\"https://example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value("abc123"));
    }
}
```

### Running API Tests

```bash
# Run all API tests
mvn test -Dtest="*ApiTest*"

# Run with specific profile
mvn test -Dspring.profiles.active=test
```

## Development

### Code Style
- Follow Spring Boot conventions
- Use meaningful variable and method names
- Add comprehensive documentation
- Write tests before implementation (TDD)

### Database Migrations
When making schema changes:
1. Update entity classes
2. Test with H2 (development)
3. Verify with PostgreSQL (production)

### Adding New Features
1. Write tests first (TDD approach)
2. Implement the feature
3. Ensure all tests pass
4. Update documentation

## Troubleshooting

### Common Issues

#### Port Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or use different port
mvn spring-boot:run -Dserver.port=8081
```

#### Database Connection Issues
```bash
# For H2 console access
# Visit: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:testdb
# Username: sa
# Password: (leave blank)
```

#### Test Failures
```bash
# Run with verbose output
mvn test -Dtest=FailedTestClass -DforkCount=1 -DreuseForks=false

# Debug specific test
mvn test -Dtest=TestClass#testMethod -Dmaven.surefire.debug=true
```

### Logs
Application logs are available at:
- Console output during development
- `logs/` directory in production
- Spring Boot Actuator endpoints: `/actuator/loggers`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Write tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## License

This project is licensed under the MIT License.