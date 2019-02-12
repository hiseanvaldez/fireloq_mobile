package com.hiseanvaldez.fireloq;

import com.google.firebase.Timestamp;

public class Model_Logs {
    public String action;
    public Timestamp datetime;

    public Model_Logs() {
    }

    public Model_Logs(String action, Timestamp datetime) {
        this.action = action;
        this.datetime = datetime;
    }

    public String getAction() {
        return action;
    }

    public Timestamp getDatetime() {
        return datetime;
    }
}
