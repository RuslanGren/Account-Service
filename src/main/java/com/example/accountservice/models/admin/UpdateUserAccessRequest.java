package com.example.accountservice.models.admin;

public class UpdateUserAccessRequest {
    private String user;
    private String operation;

    public UpdateUserAccessRequest() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
