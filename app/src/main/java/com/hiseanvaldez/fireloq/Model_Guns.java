package com.hiseanvaldez.fireloq;

import com.google.firebase.Timestamp;

public class Model_Guns {
    private String manufacturer;
    private String model;
    private String serial_number;
    private Timestamp expiry_date;
    private Timestamp registration_date;

    public Model_Guns() {
    }

    public Model_Guns(String manufacturer, String model, String serial_number, Timestamp expiry_date, Timestamp registration_date) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.serial_number = serial_number;
        this.expiry_date = expiry_date;
        this.registration_date = registration_date;
    }

    String getManufacturer() {
        return manufacturer;
    }

    String getModel() {
        return model;
    }

    String getSerial_number() {
        return serial_number;
    }

    Timestamp getExpiry_date() {
        return expiry_date;
    }

    Timestamp getRegistration_date() {
        return registration_date;
    }
}
