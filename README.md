# Sample backend for web application with courses.

Based on Udemy structure.

## API
REST API has a bunch of endpoints to get the data like courses or categories. Some of them are secured based on user authority on page. API is also documented with **Swagger**.

## Authentication
Authentication is being proceed by **Spring Security** based on [JWT](https://jwt.io/).
Each request to this backend is catched by JWT request filter and there user is being authenticated or not.
Backend is stateless, so every time user must provide Json Web Token to pass the secure layers to particular secured endpoint.

User can get a JWT just by making request to authentication resource with credentials provided.

Users password are saved in database as **bcrypt hash**.

## Database
Default database for application is **Postgres** v10.

## Docker
Project provides also predefined *Dockerfile* and *docker-compose.yml*. Docker compose consists of **postgres** database and backend application. 

> Make sure to change datasource url in *appliaction.properties* if needed.

To execute docker compose type (from root directory):
```
docker compose up
```

### Technologies and libraries
- Java 16
- Maven
- Spring Boot 2.5.2
- Spring Security
- Spring Actuator
- JPA / Hibernate
- Postgresql connector
- Lombok
- Swagger
- JJWT
- JUnit 5
- Mockito
- Hamcrest
