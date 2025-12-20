package com.example.ecoexchange.model;

public class Admin extends User {
    public Admin(int userID, String fullName, String username, String password) {
        super(userID, fullName, username, password, Role.ADMIN);
    }

    @Override
    public String getDashboardViewName() {
        return "admin_dashboard.fxml";
    }
}
