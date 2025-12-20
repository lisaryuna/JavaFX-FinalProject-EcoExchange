module com.example.ecoexchange {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires java.desktop;

    opens com.example.ecoexchange to javafx.fxml;
    exports com.example.ecoexchange;

    opens com.example.ecoexchange.controller to javafx.fxml;
    exports com.example.ecoexchange.controller;

    opens com.example.ecoexchange.model to javafx.fxml;
    exports com.example.ecoexchange.model;

    exports com.example.ecoexchange.database;
    exports com.example.ecoexchange.dao;
    exports com.example.ecoexchange.service;
}