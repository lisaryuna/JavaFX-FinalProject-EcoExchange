package com.example.ecoexchange.dao;

import com.example.ecoexchange.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WithdrawalDAO {
    public boolean createWithdrawal(int userID, double amount) {
        String query = "INSERT INTO withdrawal (userID, amount) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userID);
            statement.setDouble(2, amount);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
