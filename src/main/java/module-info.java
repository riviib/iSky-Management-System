module se2203b.assignments {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // Spring / Spring Boot
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;
    requires spring.core;

    // JPA + H2
    requires spring.data.jpa;
    requires jakarta.persistence;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires java.prefs;

    // Open package for reflection (Spring, JavaFX, Hibernate, etc.)
    opens se2203b.assignments;

    exports se2203b.assignments;
    exports se2203b.assignments.domain;
    opens se2203b.assignments.domain;
    exports se2203b.assignments.repo;
    opens se2203b.assignments.repo;
    exports se2203b.assignments.service;
    opens se2203b.assignments.service;
    exports se2203b.assignments.controller;
    opens se2203b.assignments.controller;
}
