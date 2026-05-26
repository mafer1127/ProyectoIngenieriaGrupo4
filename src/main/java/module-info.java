module com.helpdesk {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.google.gson;
    requires java.desktop;

    opens com.helpdesk to javafx.fxml;
    opens com.helpdesk.model to com.google.gson;
    exports com.helpdesk;
    exports com.helpdesk.ui;
    exports com.helpdesk.ui.views;
    exports com.helpdesk.ui.components;
    exports com.helpdesk.model;
    exports com.helpdesk.service;
    exports com.helpdesk.persistence;
    exports com.helpdesk.util;
}
