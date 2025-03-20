/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.abhiram.model;

/**
 *
 * @author FRIDAH
 */
public class Contact {
    private String username;
//    private String email;
//    private String address;
//    private String date_of_birth;
//    private String phoneNumber;
    private String maskedName;
    private String maskedPhone;

    // Constructors
//    public Contact(String username, String maskedName, String maskedPhone, String dob, String email, String address) {
//        this.username = username;
//        this.maskedName = maskedName;
//        this.maskedPhone = maskedPhone;
//        this.phoneNumber = phoneNumber;
//        this.address = address;
//        this.date_of_birth=dob;
//        this.email= email;
//    }
    public Contact(String username, String maskedName, String maskedPhone) {
    this.username = username;
    this.maskedName = maskedName;
    this.maskedPhone = maskedPhone;
    }


    // Getters and setters
    public String getUsername() {
        return username;
    }
//    public String getEmail() {
//        return email;
//    }
//    public String getphoneNumber() {
//        return phoneNumber;
//    }
//    public String getAdress() {
//        return address;
//    }
//    public String getDob() {
//        return date_of_birth;
//    }
    public void setUsername(String username) {
        this.username = username;
    }
//    public void setEmail(String email) {
//        this.email = email;
//    }
//    public void setPhoneNumber(String phonenumber) {
//        this.phoneNumber =phonenumber;
//    }
//    public void setAddress(String address) {
//        this.phoneNumber =address;
//    }

    public String getMaskedName() {
        return maskedName;
    }

    public void setMaskedName(String maskedName) {
        this.maskedName = maskedName;
    }

    public String getMaskedPhone() {
        return maskedPhone;
    }

    public void setMaskedPhone(String maskedPhone) {
        this.maskedPhone = maskedPhone;
    }
    
}
