package com.example.ecoexchange.model;

public abstract class User {
    private int userID;
    private String fullName;
    private String username;
    private String password;
    private Role role;

    public User(int userID, String fullName, String username, String password, Role role) {
        this.userID = userID;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getUserID() {
        return userID;
    }
    public String getFullName() {
        return fullName;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public Role getRole() {
        return role;
    }

    public abstract String getDashboardViewName();
}
