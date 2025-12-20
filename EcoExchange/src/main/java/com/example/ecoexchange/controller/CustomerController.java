package com.example.ecoexchange.controller;

import com.example.ecoexchange.dao.WasteDAO;
import com.example.ecoexchange.model.Customer;
import com.example.ecoexchange.model.WasteCategory;
import com.example.ecoexchange.service.TransactionService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
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

        ComboBox<WasteCategory> comboWaste =  new ComboBox<>();
        comboWaste.getItems().addAll(wasteDAO.getAllCategories());

        TextField txtWeight =  new TextField();
        txtWeight.setPromptText("Example: 5.5");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new  Label("Waste Type:"), 0, 0);
        grid.add(comboWaste, 1, 0);
        grid.add(new  Label("Weight:"), 0, 1);
        grid.add(txtWeight, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

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
