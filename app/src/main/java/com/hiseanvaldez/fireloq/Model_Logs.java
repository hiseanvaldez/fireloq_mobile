package com.hiseanvaldez.fireloq;

import com.google.firebase.Timestamp;

public class Model_Logs {
    private String action;
    private String user_id;
    private String log_type;
    private Timestamp datetime;

    public Model_Logs() {
    }

    public Model_Logs(String action, String user_id, String log_type, Timestamp datetime) {
        this.action = action;
        this.user_id = user_id;
        this.log_type = log_type;
        this.datetime = datetime;
    }

    String getUser_id() {
        return user_id;
    }

    String getAction() {
        return action;
    }

    String getLog_type() {
        return log_type;
    }

    Timestamp getDatetime() {
        return datetime;
    }
}
