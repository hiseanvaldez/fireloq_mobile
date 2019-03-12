package com.hiseanvaldez.fireloq;

import com.google.firebase.Timestamp;

public class Model_Users {
    private String address;
    private Timestamp bday;
    private String email;
    private String first_name;
    private String gender;
    private String landline_number;
    private String last_name;
    private Timestamp license_expiry;
    private String license_number;
    private String mobile_number;
    private String user_id;

    public Model_Users() {
    }

    public Model_Users(String address, Timestamp bday, String email, String first_name, String gender, String landline_number, String last_name, Timestamp license_expiry, String license_number, String mobile_number, String user_id) {
        this.address = address;
        this.bday = bday;
        this.email = email;
        this.first_name = first_name;
        this.gender = gender;
        this.landline_number = landline_number;
        this.last_name = last_name;
        this.license_expiry = license_expiry;
        this.license_number = license_number;
        this.mobile_number = mobile_number;
        this.user_id = user_id;
    }

    public String getAddress() {
        return address;
    }

    public Timestamp getBday() {
        return bday;
    }

    public String getEmail() {
        return email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getGender() {
        return gender;
    }

    public String getLandline_number() {
        return landline_number;
    }

    public String getLast_name() {
        return last_name;
    }

    public Timestamp getLicense_expiry() {
        return license_expiry;
    }

    public String getLicense_number() {
        return license_number;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public String getUser_id() {
        return user_id;
    }
}
