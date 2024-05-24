Certainly! Here's a suggested `README.md` for your project, documenting the setup, configuration, and main features:

# GymBuddy

GymBuddy is a Spring Boot application for managing users. It includes features for user registration, authentication, and user data management. The application uses Spring Security for authentication and authorization, Spring Data JPA for database access, and H2 as an in-memory database.

## Table of Contents

- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Endpoints](#endpoints)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [Contributing](#contributing)
- [License](#license)

## Getting Started

### Prerequisites

- Java 17
- Maven

### Installation

1. Clone the repository:

   ```sh
   git clone https://github.com/yourusername/gymbuddy.git
   cd gymbuddy
   ```

2. Build the project:

   ```sh
   mvn clean install
   ```

3. Run the application:

   ```sh
   mvn spring-boot:run
   ```

4. The application will be available at `http://localhost:8080`.

### Access H2 Console

- Access the H2 console at `http://localhost:8080/h2-console`
- Use the following settings:
  - **JDBC URL**: `jdbc:h2:file:./gymbuddy`
  - **User Name**: `SA`
  - **Password**: _(leave empty)_

## Project Structure

```plaintext
src/main/java/com/sforce/gymbuddy
├── config
│   └── SecurityConfiguration.java
├── controller
│   └── UserController.java
├── dto
│   └── UserDTO.java
├── exception
│   └── GlobalExceptionHandler.java
├── model
│   └── User.java
├── repository
│   └── UserRepository.java
├── service
│   ├── CustomUserDetailsService.java
│   └── UserService.java
└── GymbuddyApplication.java
```

## Endpoints

### User Endpoints

- **Register User**

  ```http
  POST /api/users/register
  ```

  Request Body:

  ```json
  {
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "username": "johndoe",
    "password": "password",
    "phone": "1234567890",
    "addressLine1": "123 Main St",
    "addressLine2": "",
    "city": "Anytown",
    "state": "Anystate",
    "zipCode": "12345",
    "country": "USA"
  }
  ```

- **Login User**

  ```http
  POST /api/users/login
  ```

  Request Body:

  ```json
  {
    "username": "johndoe",
    "password": "password"
  }
  ```

- **Get All Users**

  ```http
  GET /api/users/listAll
  ```

- **Find User by Email**

  ```http
  GET /api/users/find
  ```

  Request Parameters:

  - `email`: The email of the user to find.

## Configuration

### Security Configuration

The security configuration is managed in the `SecurityConfiguration` class. It sets up HTTP security, including form login, HTTP basic authentication, and CSRF protection.

### User Details Service

Two user details services are used:

- `CustomUserDetailsService`: Loads user-specific data from the database.
- `InMemoryUserDetailsManager`: Manages a user stored in memory for administrative purposes.

## JWT Authentication

The application supports JWT authentication. Use the following steps to authenticate via JWT:

1. Obtain a JWT token by sending a POST request to `/api/users/login` with valid credentials.
2. Include the JWT token in the `Authorization` header for subsequent requests.

### Example

**Request**:

````http
POST /api/users/login
Content-Type: application/json

{
  "username": "theAdmin",
  "password": "qwerty"
}


### Password Encoding

BCrypt is used for password encoding. The `PasswordEncoder` bean is defined in the `SecurityConfiguration` class.

## Running Tests

Run the tests with Maven:

```sh
mvn test
````

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

## License

This project is licensed under the MIT License.

```

This `README.md` provides a clear overview of your project, including installation instructions, project structure, endpoints, configuration details, and testing instructions. It also includes sections for contributing and the project license, which you can customize as needed.
```
