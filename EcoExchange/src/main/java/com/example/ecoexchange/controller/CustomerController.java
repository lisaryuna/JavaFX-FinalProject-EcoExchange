package com.example.ecoexchange.controller;

import com.example.ecoexchange.dao.WasteDAO;
import com.example.ecoexchange.model.Customer;
import com.example.ecoexchange.model.WasteCategory;
import com.example.ecoexchange.service.TransactionService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class CustomerController {
    @FXML private Label lblWelcome;
    @FXML private Label lblBalance;
    @FXML private Label lblWeight;
    @FXML private Label lblLevel;

    private Customer currentCustomer;
    private TransactionService transactionService = new TransactionService();
    private WasteDAO wasteDAO = new WasteDAO();

    public void setCustomerData(Customer customer) {
        this.currentCustomer = customer;
        updateUI();
    }

    private void updateUI() {
        lblWelcome.setText("Welcome " + currentCustomer.getFullName());
        lblBalance.setText(String.format(" %,.0f", currentCustomer.getBalance()));
        lblWeight.setText(currentCustomer.getTotalWeightHistory() + " kg");
        lblLevel.setText(currentCustomer.getMembershipLevel().toString());

        String color = switch (currentCustomer.getMembershipLevel()) {
            case GOLD -> "#f1c40f";
            case SILVER -> "#95a5a6";
            default -> "#cd7f32";
        };
        lblLevel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-font-size: 24;");
    }

    @FXML
    public void handleDeposit(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Deposit Waste");
        dialog.setHeaderText("Select waste type & enter weight (kg)");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ecoexchange/deposit_form.fxml"));
            GridPane grid = loader.load();

            ComboBox<WasteCategory> comboWaste = (ComboBox<WasteCategory>) loader.getNamespace().get("comboWaste");
            TextField txtWeight = (TextField) loader.getNamespace().get("txtWeight");

            comboWaste.getItems().addAll(wasteDAO.getAllCategories());

            dialog.getDialogPane().setContent(grid);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    WasteCategory selectedCategory = comboWaste.getValue();
                    String weightStr = txtWeight.getText();

                    if (selectedCategory == null || weightStr.isEmpty()) {
                        showInfo("Failed", "Please select a waste category and enter weight!");
                        return;
                    }

                    double weight = Double.parseDouble(weightStr);

                    transactionService.processDeposit(currentCustomer, selectedCategory, weight);

                    updateUI();

                    showInfo("Success!", "Balance updated & Level refreshed.");
                } catch (NumberFormatException e) {
                    showInfo("Error", "Weight must be a valid number! (Use dot for decimals)");
                } catch (Exception e) {
                    e.printStackTrace();
                    showInfo("System Error", "An error occured: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            showInfo("System Error", "Failed to load dialog UI: " + e.getMessage());
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    public void handleWithdraw(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Withdraw Funds");
        dialog.setHeaderText("Current Balance: Rp " + String.format("%.0f", currentCustomer.getBalance()));
        dialog.setContentText("Enter amount to withdraw (Rp): ");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);

                if (amount <= 0) {
                    showInfo("Error", "Amount must be greater than 0!");
                    return;
                }

                transactionService.processWithdrawal(currentCustomer, amount);
                updateUI();
                showInfo("Success!", "Withdraw Successful. Funds transferred.");
            } catch (NumberFormatException e) {
                showInfo("Error", "Please enter a valid number!");
            } catch (Exception e) {
                e.printStackTrace();
                showInfo("System Error", "An error occured: " + e.getMessage());
            }
        });
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
