package com.example.ecoexchange.dao;

import com.example.ecoexchange.database.DatabaseConnection;
import com.example.ecoexchange.model.WasteCategory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WasteDAO {
    public List<WasteCategory> getAllCategories() {
        List<WasteCategory> list = new ArrayList<>();
        String query = "SELECT * FROM waste_categories";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                list.add(new WasteCategory(
                        resultSet.getInt("category_id"),
                        resultSet.getString("category_name"),
                        resultSet.getDouble("base_price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updatePrice (int categoryID, double newPrice) {
        String query = "UPDATE waste_categories SET base_price = base_price + ? WHERE category_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDouble(1, newPrice);
            statement.setInt(2, categoryID);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
