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
