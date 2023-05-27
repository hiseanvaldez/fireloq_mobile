package com.hiseanvaldez.fireloq;

import com.google.firebase.Timestamp;

public class Model_Logs {
    private String action;
    private String user_id;
    private String log_type;
    private String datetime;

    public Model_Logs() {
    }

    public Model_Logs(String action, String user_id, String log_type, String datetime) {
        this.action = action;
        this.user_id = user_id;
        this.log_type = log_type;
        this.datetime = datetime;
    }

    public String getAction() {
        return action;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getLog_type() {
        return log_type;
    }

    public String getDatetime() {
        return datetime;
    }
}
