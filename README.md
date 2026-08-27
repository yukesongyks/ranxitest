# ranxitest

A demo repository showcasing a simple Spring Boot web application built with Java 17, Spring Data JPA, Thymeleaf, and an in-memory H2 database.

## Repository Structure

```
ranxitest
└── my-spring-boot-app      # Spring Boot application
    ├── src
    │   ├── main
    │   │   ├── java/com/example/myapp
    │   │   │   ├── MyAppApplication.java       # Application entry point
    │   │   │   ├── controllers
    │   │   │   │   └── HomeController.java     # HTTP request handlers
    │   │   │   ├── models
    │   │   │   │   └── User.java               # JPA entity
    │   │   │   ├── repositories
    │   │   │   │   └── UserRepository.java     # Spring Data repository
    │   │   │   └── services
    │   │   │       └── UserService.java        # Business logic
    │   │   └── resources
    │   │       ├── application.properties      # Application configuration
    │   │       └── templates
    │   │           └── index.html              # Thymeleaf template
    │   └── test
    │       └── java/com/example/myapp
    │           └── MyAppApplicationTests.java  # Integration tests
    └── pom.xml
```

## Tech Stack

| Technology | Version |
|---|---|
| Java | 17 |
| Spring Boot | 2.6.6 |
| Spring Data JPA | via Spring Boot |
| Thymeleaf | via Spring Boot |
| H2 Database | in-memory, runtime |
| Maven | build tool |

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Clone the Repository

```bash
git clone https://github.com/yukesongyks/ranxitest.git
cd ranxitest
```

### Build and Run

```bash
cd my-spring-boot-app
mvn clean install
mvn spring-boot:run
```

The application starts on **http://localhost:8080**.

### Running Tests

```bash
cd my-spring-boot-app
mvn test
```

## API Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/` | Returns the home page |

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

## License

This project is licensed under the MIT License.
test push at Mon Jul 13 18:18:53 CST 2026
