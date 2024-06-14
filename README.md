# GymBuddy

GymBuddy is a Spring Boot application for managing users. It includes features for user registration, authentication, and user data management. The application uses Spring Security for authentication and authorization, Spring Data JPA for database access, and H2 as an in-memory database. HTTPS and RSA encryption are used for secure communication.

## Table of Contents

- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Endpoints](#endpoints)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [RSA Key and Certificate Generation](#rsa-key-and-certificate-generation)
- [Configuring SSL for HTTPS](#configuring-ssl-for-https)
- [Getting a JWT Token](#getting-a-jwt-token)
- [Postman Collection](#postman-collection)
- [VSCode Tasks](#vscode-tasks)
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

3. Run the application with the necessary environment variables:

   ```sh
   KEY_STORE_PWD_GYMBUDDY=080654 JWT_SECRET_KEY=Mcb0-0886 mvn spring-boot:run
   ```

4. The application will be available at `https://localhost:8443`.

### Access H2 Console

- Access the H2 console at `https://localhost:8443/h2-console`
- Use the following settings:
  - **JDBC URL**: `jdbc:h2:file:./gymbuddy`
  - **User Name**: `SA`
  - **Password**: _(leave empty)_

## Project Structure

```plaintext
src/main/java/com/sforce/gymbuddy
├── GymbuddyApplication.java
├── config
│   ├── RSAKeyConfig.java
│   ├── SecurityConfiguration.java
│   ├── TomcatHttpRedirectConfig.java
│   └── WebConfig.java
├── controller
│   ├── HomeController.java
│   ├── PlaceholderController.java
│   ├── PublicKeyController.java
│   └── UserController.java
├── dto
│   ├── UserCreateDTO.java
│   └── UserDTO.java
├── exception
│   └── GlobalExceptionHandler.java
├── filter
│   └── JwtRequestFilter.java
├── model
│   └── User.java
├── repository
│   └── UserRepository.java
├── service
│   ├── CustomUserDetailsService.java
│   └── UserService.java
└── util
    ├── JwtUtil.java
    └── RSAUtil.java
```

## Endpoints

### Public Key Endpoint

- **Get Public Key**

  ```http
  GET /api/public-key
  ```

### User Endpoints

- **Register User**

  ```http
  POST /api/users/register
  ```

- **Login User**

  ```http
  POST /api/users/login
  ```

- **Get All Users**

  ```http
  GET /api/users/listAll
  ```

- **Find User by Email**

  ```http
  GET /api/users/find
  ```

- **Find User by Username**

  ```http
  GET /api/users/getUser
  ```

- **Update User**

  ```http
  PUT /api/users/update
  ```

- **Delete User**

  ```http
  DELETE /api/users/delete/{userId}
  ```

- **Update Password**

  ```http
  PUT /api/users/updatePassword/{userId}
  ```

### Placeholder Endpoint

- **Get Placeholder Data**

  ```http
  GET /api/placeholder
  ```

## Configuration

### Security Configuration

- **SecurityConfiguration.java**: Manages HTTP security, including form login, HTTP basic authentication, and CSRF protection.

### User Details Service

- **CustomUserDetailsService.java**: Loads user-specific data from the database.

### JWT Authentication

- **JwtRequestFilter.java**: Manages JWT authentication and authorization.

## Running Tests

Run the tests with Maven:

```sh
mvn test
```

## RSA Key and Certificate Generation

### Steps to Generate RSA Private Key and Certificate

1. **Generate a Private Key**

   ```sh
   openssl genpkey -algorithm RSA -out pkcs8.pem -aes256
   ```

2. **Generate a Public Key**

   ```sh
   openssl rsa -pubout -in pkcs8.pem -out public.key
   ```

3. **Verify the Key Files**

   ```sh
   openssl rsa -in pkcs8.pem -check
   openssl rsa -in public.key -pubin -text -noout
   ```

## Configuring SSL for HTTPS

### Steps to Generate the SSL Certificate

1. **Generate a Keystore**

   ```sh
   keytool -genkeypair -alias gymbuddy -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 365
   ```

2. **Store the Keystore Securely**

   Ensure that the `keystore.p12` file is stored securely and is not committed to version control.

3. **Set Environment Variables**

   ```sh
   export KEY_STORE_PWD_GYMBUDDY=your_keystore_password
   ```

4. **Update Application Properties**

   ```properties
   server.ssl.key-store=classpath:keystore.p12
   server.ssl.key-store-password=${KEY_STORE_PWD_GYMBUDDY}
   server.ssl.key-store-type=PKCS12
   server.ssl.key-alias=gymbuddy
   server.port=8443
   ```

## Getting a JWT Token

### Using Node.js to Get a JWT Token

Use the provided Node.js script in the `scripts/getToken` directory to fetch the public key, encrypt the password, and obtain a JWT token.

### Using Postman with JWT Token

1. **Set Up Postman to Trust the Self-Signed Certificate**:

   - In Postman, go to `File > Settings`.
   - Under the `General` tab, disable `SSL certificate verification`.

2. **Add the JWT Token to Your Requests**:
   - For each request, go to the `Authorization` tab.
   - Select `Bearer Token` from the `Type` dropdown.
   - Paste the JWT token obtained from the Node.js script into the `Token` field.

## Postman Collection

Import the provided Postman collection (`GymBuddy.postman_collection.json`) to test the API endpoints. Example requests include user registration, login, and fetching user details.

## VSCode Tasks

A `tasks.json` file is provided for running the GymBuddy application and tests in VSCode.

```json
{
  "version": "2.0.0",
  "tasks": [
    {
      "type": "shell",
      "label": "run gymbuddy",
      "command": "KEY_STORE_PWD_GYMBUDDY=080654 JWT_SECRET_KEY=Mcb0-0886 mvn spring-boot:run",
      "problemMatcher": [],
      "isBackground": true
    },
    {
      "type": "shell",
      "label": "test gymbuddy",
      "command": "mvn test",
      "problemMatcher": []
    },
    {
      "type": "shell",
      "label": "get JWT token",
      "command": "node scripts/getToken/encrypt.js",
      "problemMatcher": []
    }
  ]
}
```

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

## License

Private use only by the author.
