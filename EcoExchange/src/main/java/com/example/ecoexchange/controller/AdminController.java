package com.example.ecoexchange.controller;

import com.example.ecoexchange.dao.WasteDAO;
import com.example.ecoexchange.model.Admin;
import com.example.ecoexchange.model.WasteCategory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class AdminController {
    @FXML private Label lblWelcome;
    @FXML private TableView<WasteCategory> tblWaste;
    @FXML private TableColumn<WasteCategory,String> colName;
    @FXML private TableColumn<WasteCategory,Double> colPrice;

    @FXML private TextField txtName;
    @FXML private TextField txtPrice;

    private WasteDAO wasteDAO = new WasteDAO();
    private WasteCategory selectedWaste;

    public void setAdminData(Admin admin) {
        lblWelcome.setText("Welcome, " + admin.getFullName());

        loadWasteData();
        setupTableSelection();
    }

    private void loadWasteData() {
        List<WasteCategory> wasteCategoryList = wasteDAO.getAllCategories();
        ObservableList<WasteCategory> data = FXCollections.observableArrayList(wasteCategoryList);

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("basePrice"));
        tblWaste.setItems(data);
    }

    private void setupTableSelection() {
        tblWaste.getSelectionModel().selectedItemProperty().addListener((observable, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedWaste = newSelection;
                txtName.setText(selectedWaste.getName());
                txtPrice.setText(String.valueOf(selectedWaste.getBasePrice()));
            }
        });
    }

    @FXML
    public void handleUpdate(ActionEvent event) {
        if (selectedWaste == null) {
            showAlert("Warning", "Please select a waste category from the table first.");
            return;
        }

        try {
            double newPrice =  Double.parseDouble(txtPrice.getText());

            if (newPrice <= 0) {
                showAlert("Invalid Price", "Price must be greater than 0.");
                return;
            }

            boolean success = wasteDAO.updatePrice(selectedWaste.getId(), newPrice);

            if (success) {
                showAlert("Success", "Price updated successfully!");
                loadWasteData();
                txtPrice.clear();
                txtName.clear();
                selectedWaste = null;
            } else {
                showAlert("Error", "Price update failed!");
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Price must be a valid number!");
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void handleAddWaste(ActionEvent event) {
        String name = txtName.getText();
        String priceText = txtPrice.getText();

        if (name.isEmpty() || priceText.isEmpty()) {
            showAlert("Error", "Please fill all the fields in your fields.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);


            WasteCategory newCategory = new WasteCategory(name, price);

            boolean success = wasteDAO.addCategory(newCategory);
            if (success) {
                showAlert("Success", "New category added!");
                loadWasteData();
                txtName.clear();
                txtPrice.clear();
            } else {
                showAlert("Error", "Failed to add category!");
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Price must be a valid number!");
        }
    }

    @FXML
    public void handleDeleteWaste(ActionEvent event) {
        if (selectedWaste == null) {
            showAlert("Warning", "Please select a waste category from the table first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete: " + selectedWaste.getName() + "?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            boolean success = wasteDAO.deleteCategory(selectedWaste.getId());

            if (success) {
                showAlert("Success", "Category deleted!");
                loadWasteData();
                txtName.clear();
                txtPrice.clear();
                selectedWaste = null;
            } else {
                showAlert("Error", "Failed to delete category.");
            }
        }
    }
}
