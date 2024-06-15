package com.sforce.sforcetrading.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents a user in the system.
 */
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = { "username", "email" }))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Username is mandatory")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password should have at least 6 characters")
    private String password;

    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    // Getters and Setters

    /**
     * Gets the ID of the user.
     *
     * @return the ID of the user
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the user.
     *
     * @param id the new ID of the user
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the first name of the user.
     *
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the user.
     *
     * @param firstName the new first name of the user
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the user.
     *
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the user.
     *
     * @param lastName the new last name of the user
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the email of the user.
     *
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user.
     *
     * @param email the new email of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the phone number of the user.
     *
     * @return the phone number of the user
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number of the user.
     *
     * @param phone the new phone number of the user
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets the password of the user.
     *
     * @return the password of the user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password the new password of the user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the first address line of the user.
     *
     * @return the first address line of the user
     */
    public String getAddressLine1() {
        return addressLine1;
    }

    /**
     * Sets the first address line of the user.
     *
     * @param addressLine1 the new first address line of the user
     */
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    /**
     * Gets the second address line of the user.
     *
     * @return the second address line of the user
     */
    public String getAddressLine2() {
        return addressLine2;
    }

    /**
     * Sets the second address line of the user.
     *
     * @param addressLine2 the new second address line of the user
     */
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    /**
     * Gets the city of the user.
     *
     * @return the city of the user
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city of the user.
     *
     * @param city the new city of the user
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the state of the user.
     *
     * @return the state of the user
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state of the user.
     *
     * @param state the new state of the user
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the zip code of the user.
     *
     * @return the zip code of the user
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Sets the zip code of the user.
     *
     * @param zipCode the new zip code of the user
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Gets the country of the user.
     *
     * @return the country of the user
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country of the user.
     *
     * @param country the new country of the user
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the username of the user.
     *
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username the new username of the user
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
