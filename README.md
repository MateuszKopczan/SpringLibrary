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

You can test the action at the link: https://spring-library-1.herokuapp.com

## Technology stack

- Java 11
- Spring Boot
- Hibernate
- Spring Security
- Thymeleaf
- MySQL
- Lombok
- JUnit
- Mockito
