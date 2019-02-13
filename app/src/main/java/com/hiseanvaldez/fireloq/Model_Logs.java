package com.hiseanvaldez.fireloq;

import com.google.firebase.Timestamp;

public class Model_Logs {
    public String action;
    public String user_id;
    public String log_type;
    public Timestamp datetime;

    public Model_Logs() {
    }

    public Model_Logs(String action, String user_id, String log_type, Timestamp datetime) {
        this.action = action;
        this.user_id = user_id;
        this.log_type = log_type;
        this.datetime = datetime;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getAction() {
        return action;
    }

    public String getLog_type() {
        return log_type;
    }

    public Timestamp getDatetime() {
        return datetime;
    }
}
