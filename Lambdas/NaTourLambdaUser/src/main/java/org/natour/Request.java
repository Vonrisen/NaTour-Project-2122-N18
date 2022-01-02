package org.natour;

import org.natour.entities.User;

public class Request {

    private User user;

    private String confirmation_code;

    private String action;

    private String id_token;

    private String refresh_token;

    public Request(){}

    public Request(User user, String confirmation_code, String action, String id_token, String refresh_token) {
        this.user = user;
        this.confirmation_code = confirmation_code;
        this.action = action;
        this.id_token = id_token;
        this.refresh_token = refresh_token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getConfirmation_code() {
        return confirmation_code;
    }

    public void setConfirmation_code(String confirmation_code) {
        this.confirmation_code = confirmation_code;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getId_token() {
        return id_token;
    }

    public void setId_token(String id_token) {
        this.id_token = id_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
}
