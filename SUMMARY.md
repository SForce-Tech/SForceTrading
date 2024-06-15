### sforcetradingApplication.java

**Package:** `com.sforce.sforcetrading`

- The main class that bootstraps the Spring Boot application.
- Contains the `main` method which calls `SpringApplication.run(sforcetradingApplication.class, args)`.

### RSAKeyConfig.java

**Package:** `com.sforce.sforcetrading.config`

- Configuration class for loading the RSA private key used for encryption and decryption.
- **Fields:**
  - `privateKeyPath`: Path to the RSA private key file.
- **Methods:**
  - `privateKey()`: Loads and returns the RSA private key from the specified file path.

### SecurityConfiguration.java

**Package:** `com.sforce.sforcetrading.config`

- Configuration class for Spring Security.
- **Fields:**
  - `customUserDetailsService`: Service to load user-specific data.
  - `jwtRequestFilter`: Filter to handle JWT validation.
- **Methods:**
  - `securityFilterChain(HttpSecurity http, AuthenticationManager authManager)`: Configures the security filter chain.
  - `inMemoryUserDetailsManager(PasswordEncoder passwordEncoder)`: Configures an in-memory user details manager with a default admin user.
  - `passwordEncoder()`: Configures a BCrypt password encoder.
  - `authManager(HttpSecurity http)`: Configures the authentication manager with both custom and in-memory user details services.
  - `userDetailsService(InMemoryUserDetailsManager inMemoryUserDetailsManager)`: Configures a composite user details service.

### TomcatHttpRedirectConfig.java

**Package:** `com.sforce.sforcetrading.config`

- Configuration class for redirecting HTTP requests to HTTPS using Tomcat.
- **Methods:**
  - `servletContainerCustomizer()`: Customizes the TomcatServletWebServerFactory to add an HTTP to HTTPS redirect connector.
  - `httpToHttpsRedirectConnector()`: Creates an HTTP connector that redirects to HTTPS.

### WebConfig.java

**Package:** `com.sforce.sforcetrading.config`

- Configuration class for CORS settings.
- **Methods:**
  - `addCorsMappings(CorsRegistry registry)`: Configures CORS mappings.

### HomeController.java

**Package:** `com.sforce.sforcetrading.controller`

- Simple controller for handling the home endpoint.
- **Methods:**
  - `home()`: Returns a welcome message.

### PublicKeyController.java

**Package:** `com.sforce.sforcetrading.controller`

- Controller to expose the public key for encryption.
- **Fields:**
  - `publicKeyPath`: Path to the RSA public key file.
- **Methods:**
  - `getPublicKey()`: Retrieves and returns the public key.

### UserController.java

**Package:** `com.sforce.sforcetrading.controller`

- Controller for managing users.
- **Fields:**
  - `userService`: Service for user operations.
  - `jwtUtil`: Utility for JWT operations.
  - `privateKey`: RSA private key for decrypting passwords.
- **Methods:**
  - `getAllUsers()`: Retrieves and returns a list of all users.
  - `getUserByEmail(String email)`: Retrieves and returns a user by their email.
  - `registerUser(UserCreateDTO userCreateDTO)`: Registers a new user.
  - `loginUser(User loginRequest)`: Authenticates a user and returns a JWT token.
  - `updateUser(UserDTO userDTO)`: Updates an existing user.
  - `deleteUser(Long userId)`: Deletes a user by their ID.

### UserCreateDTO.java

**Package:** `com.sforce.sforcetrading.dto`

- Data Transfer Object for user creation.
- **Fields:**
  - `id`, `firstName`, `lastName`, `email`, `username`, `password`, `phone`, `addressLine1`, `addressLine2`, `city`, `state`, `zipCode`, `country`: Various user details with validation annotations.

### UserDTO.java

**Package:** `com.sforce.sforcetrading.dto`

- Data Transfer Object for user data transfer.
- **Fields:**
  - `id`, `firstName`, `lastName`, `email`, `username`, `phone`, `addressLine1`, `addressLine2`, `city`, `state`, `zipCode`, `country`: Various user details.

### GlobalExceptionHandler.java

**Package:** `com.sforce.sforcetrading.exception`

- Global exception handler for handling exceptions across the application.
- **Methods:**
  - `handleValidationExceptions(MethodArgumentNotValidException ex)`: Handles validation exceptions.
  - `handleAllExceptions(Exception ex)`: Handles all other exceptions.

### JwtRequestFilter.java

**Package:** `com.sforce.sforcetrading.filter`

- Filter for handling JWT validation on incoming requests.
- **Fields:**
  - `userDetailsService`: Service to load user-specific data.
  - `jwtUtil`: Utility for JWT operations.
- **Methods:**
  - `doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)`: Extracts and validates the JWT token, then sets the authentication context.

### User.java

**Package:** `com.sforce.sforcetrading.model`

- Represents a user in the system.
- **Fields:**
  - `id`, `firstName`, `lastName`, `email`, `username`, `password`, `phone`, `addressLine1`, `addressLine2`, `city`, `state`, `zipCode`, `country`: Various user details with validation annotations.

### UserRepository.java

**Package:** `com.sforce.sforcetrading.repository`

- Repository interface for CRUD operations on `User` entities.
- **Methods:**
  - `findByEmail(String email)`: Finds a user by their email.
  - `findByUsername(String username)`: Finds a user by their username.

### CustomUserDetailsService.java

**Package:** `com.sforce.sforcetrading.service`

- Service for loading user-specific data.
- **Fields:**
  - `userRepository`: Repository for user operations.
- **Methods:**
  - `loadUserByUsername(String username)`: Loads a user by username and constructs a `UserDetails` object.

### UserService.java

**Package:** `com.sforce.sforcetrading.service`

- Service for managing users.
- **Fields:**
  - `userRepository`: Repository for user operations.
  - `passwordEncoder`: Encoder for encoding passwords.
- **Methods:**
  - `getAllUsers()`: Retrieves all users.
  - `getUserByEmail(String email)`: Retrieves a user by email.
  - `saveUser(User user)`: Saves a new user or updates an existing user.
  - `authenticateUser(String username, String rawPassword)`: Authenticates a user by username and password.
  - `updateUser(User user)`: Updates a user record in the database.
  - `deleteUser(Long userId)`: Deletes a user by their ID.
  - `convertToDTO(User user)`: Converts a `User` entity to a `UserDTO`.
  - `convertToUserDetails(User user)`: Converts a `User` entity to a `UserDetails`.

### JwtUtil.java

**Package:** `com.sforce.sforcetrading.util`

- Utility class for handling JWT operations.
- **Fields:**
  - `SECRET_KEY`: Secret key for signing JWT tokens.
- **Methods:**
  - `extractUsername(String token)`: Extracts the username from the JWT token.
  - `extractExpiration(String token)`: Extracts the expiration date from the JWT token.
  - `extractClaim(String token, Function<Claims, T> claimsResolver)`: Extracts a specific claim from the JWT token.
  - `extractAllClaims(String token)`: Extracts all claims from the JWT token.
  - `isTokenExpired(String token)`: Checks if the JWT token has expired.
  - `generateToken(UserDetails userDetails)`: Generates a JWT token for the given user details.
  - `createToken(Map<String, Object> claims, String subject)`: Creates a JWT token with the given claims and subject.
  - `validateToken(String token, UserDetails userDetails)`: Validates the JWT token against the user details.
  - `refreshToken(String token)`: Refreshes the JWT token if it is about to expire.

### RSAUtil.java

**Package:** `com.sforce.sforcetrading.util`

- Utility class for RSA encryption and decryption.
- **Methods:**
  - `getPublicKey(String base64PublicKey)`: Converts a Base64 encoded public key string into a `PublicKey` object.
  - `getPrivateKey(String base64PrivateKey)`: Converts a Base64 encoded private key string into a `PrivateKey` object.
  - `encrypt(String data, PublicKey publicKey)`: Encrypts data using a public key.
  - `decrypt(byte[] data, PrivateKey privateKey)`: Decrypts data using a private key.
