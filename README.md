# Spring Library

## Generally

A simple application that simulates the activities of the shop library. The main functionalities are:

- user accounts,
- cart,
- email notifications,
- payments with PayU.

## Authorization

| Endpoint       | Authorization      |
|----------------|--------------------|
| /account/    | No Authorization   |
| /admin/**    | Authenticated admin   |
| /account/** | Authenticated user   |
| /cart/**   | Authenticated user |
| /checkout/**   | Authenticated user |

## How to run?

Before run set necessary informations to application.yml:
 - SPRING_DATASOURCE_URL e.g. "jdbc:postgresql://DATABASE_URL:5432/DATABASE_NAME"
 - SPRING_DATASOURCE_USERNAME
 - SPRING_DATASOURCE_PASSWORD
 - PAYU_DESCRIPTION 
 - PAYU_MERCHANTPOSID
 - PAYU_SECONDKEY
 - PAYU_CLIENTID
 - PAYU_CLIENTSECRET
 - PAYU_CONTINUEURL
 - PAYU_NOTIFYURL
 - MAIL_USERNAME
 - MAIL_PASSWORD

Requirements:
 - Java 11
 - Java and Maven added to environment variables

Run: <br>
 1. `mvn -U clean install` from main project directory
 2. `cd target` from main project directory
 3. `java -jar Library-0.0.1-SNAPSHOT.jar`

You can also test the action at the link: https://spring-library1.herokuapp.com

Database on heroku contains initial user and admin:

| Role | Username | Email | Password |
|------|----------|-------|----------|
| User | ExampleUser | exampleuser@springlibrary.com | user |
| Admin | ADMIN | admin@springlibrary.com | admin |

## Technology stack

- Java 11
- Spring Boot
- Hibernate
- Spring Security
- Thymeleaf
- PostgreSQL
- Lombok
- JUnit
- Mockito
