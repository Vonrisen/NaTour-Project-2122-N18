package com.cinamidea.natour_2022.auth_util;

public class Tokens {

    private String id_token;

    private String refresh_token;

    private String access_token;

    public Tokens(String id_token, String refresh_token, String access_token) {
        this.id_token = id_token;
        this.refresh_token = refresh_token;
        this.access_token = access_token;
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

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
