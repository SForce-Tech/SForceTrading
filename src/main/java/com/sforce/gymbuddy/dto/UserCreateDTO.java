package com.sforce.gymbuddy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for User creation.
 */
public class UserCreateDTO {

    private Long id;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Username is mandatory")
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

    /**
     * Gets the ID of the user.
     * 
     * @return the user's ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the user.
     * 
     * @param id the user's ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the first name of the user.
     * 
     * @return the user's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the user.
     * 
     * @param firstName the user's first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the user.
     * 
     * @return the user's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the user.
     * 
     * @param lastName the user's last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the email of the user.
     * 
     * @return the user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user.
     * 
     * @param email the user's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the username of the user.
     * 
     * @return the user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     * 
     * @param username the user's username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password of the user.
     * 
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     * 
     * @param password the user's password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the phone number of the user.
     * 
     * @return the user's phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number of the user.
     * 
     * @param phone the user's phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets the first line of the user's address.
     * 
     * @return the first line of the user's address
     */
    public String getAddressLine1() {
        return addressLine1;
    }

    /**
     * Sets the first line of the user's address.
     * 
     * @param addressLine1 the first line of the user's address
     */
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    /**
     * Gets the second line of the user's address.
     * 
     * @return the second line of the user's address
     */
    public String getAddressLine2() {
        return addressLine2;
    }

    /**
     * Sets the second line of the user's address.
     * 
     * @param addressLine2 the second line of the user's address
     */
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    /**
     * Gets the city of the user's address.
     * 
     * @return the city of the user's address
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city of the user's address.
     * 
     * @param city the city of the user's address
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the state of the user's address.
     * 
     * @return the state of the user's address
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state of the user's address.
     * 
     * @param state the state of the user's address
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the zip code of the user's address.
     * 
     * @return the zip code of the user's address
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Sets the zip code of the user's address.
     * 
     * @param zipCode the zip code of the user's address
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Gets the country of the user's address.
     * 
     * @return the country of the user's address
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country of the user's address.
     * 
     * @param country the country of the user's address
     */
    public void setCountry(String country) {
        this.country = country;
    }
}
