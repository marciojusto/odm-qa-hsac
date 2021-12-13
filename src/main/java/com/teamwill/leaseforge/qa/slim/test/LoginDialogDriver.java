package com.teamwill.leaseforge.qa.slim.test;

public class LoginDialogDriver {
    private final String userName;
    private final String password;
    private String message;
    private int loginAttempts;

    public LoginDialogDriver(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public boolean loginWithUsernameAndPassword(String userName, String password) {
        loginAttempts++;
        boolean result = this.userName.equals(userName) && this.password.equals(password);
        if (result) {
            message = String.format("%s logged in.", this.userName);
        } else {
            message = String.format("%s not logged in.", this.userName);
        }
        return result;
    }

    public String loginMessage() {
        return message;
    }

    public int numberOfLoginAttempts() {
        return loginAttempts;
    }
}