package com.example.ecoexchange.dao;

import com.example.ecoexchange.database.DatabaseConnection;
import com.example.ecoexchange.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {
    public User getUserByUsername(String username) {
        User user = null;
        String query = "SELECT * FROM users WHERE username = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("user_id");
                String fullName = resultSet.getString("full_name");
                String passDB = resultSet.getString("password");
                String roleStr = resultSet.getString("role");

                if ("admin".equalsIgnoreCase(roleStr)) {
                    user = new Admin(id, fullName, username, passDB);
                } else {
                    double balance = resultSet.getDouble("balance");
                    double weight = resultSet.getDouble("total_weight_history");
                    String level = resultSet.getString("membership_level");

                    user = new Customer(id, fullName, username, passDB, balance, weight, level);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public boolean updateCustomerStats(Customer customer) {
        String query = "UPDATE users SET balance = ?, total_weight_history = ? WHERE user_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDouble(1, customer.getBalance());
            statement.setDouble(2, customer.getTotalWeightHistory());
            statement.setString(3, customer.getMembershipLevel().toString());
            statement.setInt(4, customer.getUserID());

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
