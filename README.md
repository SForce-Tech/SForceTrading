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

- **Update User**

  ```http
  PUT /api/users/update
  ```

  Request Body:

  ```json
  {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "username": "johndoe",
    "phone": "1234567890",
    "addressLine1": "123 Main St",
    "addressLine2": "",
    "city": "Anytown",
    "state": "Anystate",
    "zipCode": "12345",
    "country": "USA"
  }
  ```

- **Delete User**

  ```http
  DELETE /api/users/delete/{userId}
  ```

  Path Variable:

  - `userId`: The ID of the user to delete.

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

```http
POST /api/users/login
Content-Type: application/json

{
  "username": "theAdmin",
  "password": "qwerty"
}
```

### Password Encoding

BCrypt is used for password encoding. The `PasswordEncoder` bean is defined in the `SecurityConfiguration` class.

## Running Tests

Run the tests with Maven:

```sh
mvn test
```

## RSA Key and Certificate Generation

To ensure secure communication, the GymBuddy application uses RSA encryption. You need to generate an RSA private key and certificate that will be used for JWT token encryption and decryption.

### Steps to Generate RSA Private Key and Certificate

1. **Generate a Private Key**

   Use the following OpenSSL command to generate a 2048-bit RSA private key:

   ```sh
   openssl genpkey -algorithm RSA -out pkcs8.pem -aes256
   ```

   This command generates a private key and saves it to a file named `pkcs8.pem`. The `-aes256` flag encrypts the key file with AES-256 encryption, and you will be prompted to set a password.

2. **Generate a Public Key**

   Extract the public key from the private key using the following command:

   ```sh
   openssl rsa -pubout -in pkcs8.pem -out public.key
   ```

   This command reads the private key from `pkcs8.pem` and writes the public key to a file named `public.key`.

3. **Verify the Key Files**

   Verify the contents of the private and public key files using the following commands:

   ```sh
   openssl rsa -in pkcs8.pem -check
   openssl rsa -in public.key -pubin -text -noout
   ```

   These commands will print the key details to the console, allowing you to verify that the keys have been generated correctly.

## Configuring SSL for HTTPS

To ensure that all communication with the server is secure, HTTPS is configured using an SSL certificate stored in a Java KeyStore (`keystore.p12`). Additionally, HTTP requests are redirected to HTTPS.

### Steps to Generate the SSL Certificate

1. **Generate a Keystore**

   Use the following command to generate a keystore with a self-signed certificate:

   ```sh
   keytool -genkeypair -alias gymbuddy -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 365
   ```

   Follow the prompts to set the keystore password and other details. This will create a `keystore.p12` file containing the private key and self-signed certificate.

### Configuring the Application

1. **Store the Keystore Securely**

   Ensure that the `keystore.p12` file is stored securely and is not committed to version control.

2. **Set Environment Variables**

   Set the environment variables for the keystore path and password:

   ```sh
   export KEY_STORE_PWD_GYMBUDDY=your_keystore_password
   ```

3. **Update Application Properties**

   Ensure the `application.properties` or `application.yml` file includes the keystore configuration:

   ```properties
   server.ssl.key-store=classpath:keystore.p12
   server.ssl.key-store-password=${KEY_STORE_PWD_GYMBUDDY}
   server.ssl.key-store-type=PKCS12
   server.ssl.key-alias=gymbuddy
   server.port=8443
   ```

### Setting Up HTTP to HTTPS Redirection

The application is configured to redirect all HTTP traffic to HTTPS.

#### Configuration Class for Redirection

Ensure you have the following configuration class:

```java
package com

.sforce.gymbuddy.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatHttpRedirectConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainerCustomizer() {
        return factory -> factory.addAdditionalTomcatConnectors(httpToHttpsRedirectConnector());
    }

    private Connector httpToHttpsRedirectConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(8080); // HTTP port
        connector.setSecure(false);
        connector.setRedirectPort(8443); // HTTPS port
        return connector;
    }
}
```

## Configuring the Application to Use the RSA Private Key

### Step 1: Store the Private Key Securely

Ensure that the `pkcs8.pem` file is stored securely and is not committed to version control.

### Step 2: Configure the Application to Use the Private Key

1. **Set the Path to the Private Key**

   The application needs to know where to find the `pkcs8.pem` file. You can set this path using an environment variable. For example:

   ```sh
   export RSA_PRIVATE_KEY_PATH=/path/to/pkcs8.pem
   ```

   Alternatively, you can set this variable in your application's environment configuration.

2. **Update the Application Configuration**

   Ensure the `application.properties` or `application.yml` file includes a reference to the environment variable:

   ```properties
   rsa.private.key.path=${RSA_PRIVATE_KEY_PATH}
   ```

### Step 3: Application Configuration for Private Key Loading

The following class is responsible for loading the RSA private key from the specified file path:

```java
package com.sforce.gymbuddy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Configuration
public class RSAKeyConfig {

    @Value("${rsa.private.key.path}")
    private String privateKeyPath;

    @Bean
    public PrivateKey privateKey() throws Exception {
        Resource resource = new ClassPathResource(privateKeyPath);
        byte[] keyBytes = Files.readAllBytes(resource.getFile().toPath());

        // Convert the key bytes into a string and clean it up
        String privateKeyPEM = new String(keyBytes)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        // Decode the Base64 string
        byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);

        // Create the private key
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}
```

## Getting a JWT Token

### Using Node.js to Get a JWT Token

You can use the following Node.js script to fetch the public key, encrypt the password, and obtain a JWT token:

```javascript
const crypto = require("crypto");
const axios = require("axios");
const https = require("https");

// Create an HTTPS agent that accepts self-signed certificates
const httpsAgent = new https.Agent({
  rejectUnauthorized: false,
});

// Function to sanitize base64 encoded string
function sanitizeBase64(str) {
  // Remove non-base64 characters, if any (e.g., newlines, spaces)
  return str.replace(/[^A-Za-z0-9+/=]/g, "");
}

// Function to encrypt the password using the public key
function encryptPassword(password, publicKeyPem) {
  const buffer = Buffer.from(password, "utf8");
  const publicKey = crypto.createPublicKey(publicKeyPem);
  const encrypted = crypto.publicEncrypt(
    {
      key: publicKey,
      padding: crypto.constants.RSA_PKCS1_PADDING,
      oaepHash: "sha256",
    },
    buffer
  );
  return encrypted.toString("base64");
}

// Fetch the public key from the API
axios
  .get("https://localhost:8443/api/public-key", { httpsAgent })
  .then((response) => {
    const publicKeyPem = response.data;
    const password = "qwerty"; // The password to encrypt

    // Encrypt the password
    let encryptedPassword = encryptPassword(password, publicKeyPem);

    // Sanitize the encrypted password
    encryptedPassword = sanitizeBase64(encryptedPassword);

    // Set the encrypted password in the request body
    const username = "leodoe"; // The username
    const requestBody = {
      username: username,
      password: encryptedPassword,
    };

    console.log("Public Key PEM:\n", publicKeyPem);
    console.log("Encrypted Password:\n", encryptedPassword);

    // Send the request with the encrypted password
    axios
      .post("https://localhost:8443/api/users/login", requestBody, {
        httpsAgent,
        headers: {
          "Content-Type": "application/json",
        },
      })
      .then((res) => {
        console.log("Response:", res.data);
      })
      .catch((err) => {
        console.error("Error in posting data:", err);
      });
  })
  .catch((error) => {
    console.error("Error fetching public key:", error);
  });
```

### Using Postman with JWT Token

Once you have obtained the JWT token using the above script, you can use it in Postman for subsequent API calls.

1. **Set Up Postman to Trust the Self-Signed Certificate**:

   - In Postman, go to `File > Settings`.
   - Under the `General` tab, disable `SSL certificate verification`.

2. **Add the JWT Token to Your Requests**:

   - For each request, go to the `Authorization` tab.
   - Select `Bearer Token` from the `Type` dropdown.
   - Paste the JWT token obtained from the Node.js script into the `Token` field.

3. **Example Request in Postman**:
   - Set the URL to `https://localhost:8443/api/users/listAll`.
   - Ensure the `Authorization` header is set with the JWT token.

This setup will allow you to interact with the GymBuddy API securely using JWT tokens for authentication.
