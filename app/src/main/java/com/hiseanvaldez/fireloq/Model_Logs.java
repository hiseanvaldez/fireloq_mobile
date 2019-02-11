package com.hiseanvaldez.fireloq;

import com.google.firebase.Timestamp;

public class Model_Logs {
    private String logTitle;
    private Timestamp logDatetime;

    public Model_Logs() {
    }

    public Model_Logs(String logTitle, Timestamp logDatetime) {
        this.logTitle = logTitle;
        this.logDatetime = logDatetime;
    }

    public String getLogTitle() {
        return logTitle;
    }

    public Timestamp getLogDatetime() {
        return logDatetime;
    }
}
