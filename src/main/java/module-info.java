module org.desarrollo.fiscalesfrontend {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.annotation;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires jdk.compiler;
    requires jdk.httpserver;
    //requires org.desarrollo.fiscalesfrontend;

    opens org.desarrollo.fiscalesfrontend to javafx.fxml;
    exports org.desarrollo.fiscalesfrontend;
    exports org.desarrollo.fiscalesfrontend.controller;
    opens org.desarrollo.fiscalesfrontend.controller to javafx.fxml;
    opens org.desarrollo.fiscalesfrontend.model to com.fasterxml.jackson.databind;
    exports org.desarrollo.fiscalesfrontend.model;
    opens org.desarrollo.fiscalesfrontend.dto to com.fasterxml.jackson.databind;
    exports org.desarrollo.fiscalesfrontend.dto;
    opens org.desarrollo.fiscalesfrontend.mapper to com.fasterxml.jackson.databind;
    exports org.desarrollo.fiscalesfrontend.mapper;

}