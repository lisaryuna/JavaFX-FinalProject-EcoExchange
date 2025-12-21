package com.example.ecoexchange.controller;

import com.example.ecoexchange.model.Admin;
import com.example.ecoexchange.model.Customer;
import com.example.ecoexchange.model.User;
import com.example.ecoexchange.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblStatus;

    private AuthService authService = new AuthService();

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Username and/or password are empty");
            return;
        }

        User loggedInUser = authService.login(username, password);

        if (loggedInUser == null) {
            lblStatus.setText("Invalid username or password");
            txtPassword.clear();
            return;
        }

        try {
            String fxmlFile = loggedInUser.getDashboardViewName();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ecoexchange/" + fxmlFile));
            Parent root = loader.load();

            if (loggedInUser instanceof Admin) {
                AdminController adminController = loader.getController();
                adminController.setAdminData((Admin) loggedInUser);
            } else if (loggedInUser instanceof Customer) {
                CustomerController customerController = loader.getController();
                customerController.setCustomerData((Customer) loggedInUser);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard - " + loggedInUser.getFullName());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            lblStatus.setText("Error System: " + e.getMessage());
        }
    }
}
