package com.example.ecoexchange.service;

import com.example.ecoexchange.dao.UserDAO;
import com.example.ecoexchange.model.User;

public class AuthService {
    private UserDAO userDAO = new UserDAO();

    public User login(String username, String password) {
        User user = userDAO.getUserByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            return user;
        } else {
            return null;
        }
    }
}
