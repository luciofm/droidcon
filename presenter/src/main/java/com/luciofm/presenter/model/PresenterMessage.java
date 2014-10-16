package com.luciofm.presenter.model;

/**
 * Created by luciofm on 10/9/14.
 */
public class PresenterMessage {
    private String method;
    private String extra;

    public PresenterMessage() {
    }

    public PresenterMessage(String method) {
        this.method = method;
    }

    public PresenterMessage(String method, String extra) {
        this.method = method;
        this.extra = extra;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "PresenterMessage{" +
                "method='" + method + '\'' +
                ", extra='" + extra + '\'' +
                '}';
    }
}